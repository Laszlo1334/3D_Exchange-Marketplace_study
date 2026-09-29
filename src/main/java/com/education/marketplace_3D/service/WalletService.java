package com.education.marketplace_3D.service;

import com.education.marketplace_3D.entities.Transaction;
import com.education.marketplace_3D.entities.User;
import com.education.marketplace_3D.entities.Wallet;
import com.education.marketplace_3D.enums.PaymentMethod;
import com.education.marketplace_3D.enums.TransactionStatus;
import com.education.marketplace_3D.enums.TransactionType;
import com.education.marketplace_3D.event.UserRegisterEvent;
import com.education.marketplace_3D.repository.TransactionRepository;
import com.education.marketplace_3D.repository.UserRepository;
import com.education.marketplace_3D.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;


    @Transactional
    public void topUpBalance(String userEmail, BigDecimal amount){
        User receiver = userRepository.findByEmail(userEmail).orElseThrow(()-> new EntityNotFoundException("User not found by Email"));
        Wallet userWallet = walletRepository.findByUserId(receiver.getId()).orElseThrow(()->new EntityNotFoundException("Wallet not found by user id"));
        userWallet.setBalance(userWallet.getBalance().add(amount));
        Transaction transaction = new Transaction(
                receiver,
                amount,
                TransactionStatus.SUCCESS,
                PaymentMethod.EXTERNAL_CARD,
                TransactionType.DEPOSIT
        );
        transactionRepository.save(transaction);
    }

    @Transactional
    @EventListener
    public void createWalletForNewUser (UserRegisterEvent event){
        User newUser = event.user();
        Wallet wallet = new Wallet(newUser);
        walletRepository.save(wallet);
    }

    @Transactional (readOnly = true)
    public BigDecimal getBalanceByUserId (UUID userId){
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("Wallet was not found by user id"));
        return wallet.getBalance();
    }
}
