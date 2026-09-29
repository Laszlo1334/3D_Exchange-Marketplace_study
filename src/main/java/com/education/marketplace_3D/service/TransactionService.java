package com.education.marketplace_3D.service;

import com.education.marketplace_3D.entities.Order;
import com.education.marketplace_3D.entities.Transaction;
import com.education.marketplace_3D.entities.User;
import com.education.marketplace_3D.entities.Wallet;
import com.education.marketplace_3D.enums.PaymentMethod;
import com.education.marketplace_3D.enums.TransactionStatus;
import com.education.marketplace_3D.enums.TransactionType;
import com.education.marketplace_3D.repository.TransactionRepository;
import com.education.marketplace_3D.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public void processPayment (Order order){
        User customer = order.getCustomer();
        User executor = order.getExecutor();
        Wallet customerWallet = walletRepository.findByUserId(customer.getId()).orElseThrow(() -> new EntityNotFoundException("Customer wallet not found"));
        Wallet executorWallet = walletRepository.findByUserId(executor.getId()).orElseThrow(() -> new EntityNotFoundException("Executor wallet not found"));
        if(customerWallet.getHoldBalance().compareTo(order.getPrice()) < 0){
            throw new IllegalStateException("There are not enough money for transaction");
        }
        customerWallet.setHoldBalance(customerWallet.getHoldBalance().subtract(order.getPrice()));
        executorWallet.setBalance(executorWallet.getBalance().add(order.getPrice()));

        Transaction transaction = new Transaction(
                order,
                customer,
                executor,
                order.getPrice(),
                TransactionStatus.SUCCESS,
                PaymentMethod.INTERNAL_WALLET,
                TransactionType.INTERNAL
                );
        transactionRepository.save(transaction);
    }

    @Transactional
    public void holdPayment (Order order){
        Wallet customerWallet = walletRepository.findByUserId(order.getCustomer().getId()).orElseThrow(()-> new EntityNotFoundException("Wallet did not find by customer id"));
        if(customerWallet.getBalance().compareTo(order.getPrice()) < 0){
            throw new IllegalStateException("Not enough money no balance");
        }
        customerWallet.setBalance(customerWallet.getBalance().subtract(order.getPrice()));
        customerWallet.setHoldBalance(customerWallet.getHoldBalance().add(order.getPrice()));
        Transaction transaction = new Transaction(
                order,
                order.getCustomer(),
                order.getCustomer(),
                order.getPrice(),
                TransactionStatus.HOLD,
                PaymentMethod.INTERNAL_WALLET,
                TransactionType.ESCROW_HOLD
        );
        transactionRepository.save(transaction);
    }

    @Transactional
    public void refundHoldPayment(Order order){
        Wallet customerWallet = walletRepository.findByUserId(order.getCustomer().getId()).orElseThrow(()-> new EntityNotFoundException("Wallet did not find by customer id"));
        customerWallet.setHoldBalance(customerWallet.getHoldBalance().subtract(order.getPrice()));
        customerWallet.setBalance(customerWallet.getBalance().add(order.getPrice()));
        Transaction transaction = new Transaction(
                order,
                order.getCustomer(),
                order.getCustomer(),
                order.getPrice(),
                TransactionStatus.REFUND,
                PaymentMethod.INTERNAL_WALLET,
                TransactionType.ESCROW_REFUND
        );
        transactionRepository.save(transaction);
    }
}
