package com.banknova.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.banknova.entity.Account;
import com.banknova.entity.Beneficiary;
import com.banknova.entity.Card;
import com.banknova.entity.Loan;
import com.banknova.entity.SpendingLimit;
import com.banknova.entity.User;
import com.banknova.entity.Wallet;
import com.banknova.repository.AccountRepository;
import com.banknova.repository.BeneficiaryRepository;
import com.banknova.repository.CardRepository;
import com.banknova.repository.LoanRepository;
import com.banknova.repository.SpendingLimitRepository;
import com.banknova.repository.UserRepository;
import com.banknova.repository.WalletRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final SpendingLimitRepository spendingLimitRepository;
    private final LoanRepository loanRepository;

    @Bean
    public CommandLineRunner run() {
        return args -> {
            User testUser = ensureUser("test@banknova.com", "Demo User", "Password123");
            User janeUser = ensureUser("jane@banknova.com", "Jane Receiver", "Password123");
            User demoUser = ensureUser("demo@banknova.com", "BankNova Demo Account", "DemoWallet123!");

            ensureWallet(testUser, new BigDecimal("1200.00"));
            ensureWallet(janeUser, new BigDecimal("540.00"));
            ensureWallet(demoUser, new BigDecimal("5000.00"));

            Account testAccount = ensureCheckingAccount(testUser, "BN-100001-000001", "Demo Checking",
                    new BigDecimal("1200.00"));
            Account janeAccount = ensureCheckingAccount(janeUser, "BN-100001-000002", "Jane Checking",
                    new BigDecimal("540.00"));
            Account demoAccount = ensureCheckingAccount(demoUser, "BN-100001-000003", "Primary Demo Account",
                    new BigDecimal("5000.00"));

            seedCardIfMissing(demoUser, demoAccount, "4242", "BankNova Demo Account", "tok_seed_demo_4242", true);
            seedCardIfMissing(testUser, testAccount, "1881", "Demo User", "tok_seed_demo_1881", false);

            seedBeneficiaryIfMissing(demoUser, janeUser, janeAccount.getAccountNumber());
            seedBeneficiaryIfMissing(testUser, demoUser, demoAccount.getAccountNumber());

            seedSpendingLimitIfMissing(demoUser, demoAccount);
            seedLoanIfMissing(demoUser, demoAccount);
        };
    }

    private User ensureUser(String email, String name, String password) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            return userRepository.save(user);
        });
    }

    private void ensureWallet(User user, BigDecimal balance) {
        walletRepository.findByUserId(user.getId()).orElseGet(() -> {
            Wallet wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(balance);
            return walletRepository.save(wallet);
        });
    }

    private Account ensureCheckingAccount(User user, String accountNumber, String accountName, BigDecimal balance) {
        return accountRepository.findByUserAndAccountType(user, "CHECKING").orElseGet(() -> {
            Account account = new Account();
            account.setUser(user);
            account.setAccountNumber(accountNumber);
            account.setAccountType("CHECKING");
            account.setAccountName(accountName);
            account.setBalance(balance);
            account.setCurrency("USD");
            account.setStatus("ACTIVE");
            account.setDailyWithdrawalLimit(new BigDecimal("5000.00"));
            account.setMonthlyTransferLimit(new BigDecimal("50000.00"));
            account.setCurrentMonthlyTransferred(BigDecimal.ZERO);
            return accountRepository.save(account);
        });
    }

    private void seedCardIfMissing(User user, Account account, String last4, String holderName, String token,
            boolean isDefault) {
        if (!cardRepository.findByUser(user).isEmpty()) {
            return;
        }

        Card card = new Card();
        card.setUser(user);
        card.setLinkedAccount(account);
        card.setTokenizedCardNumber(token);
        card.setLast4Digits(last4);
        card.setCardholderName(holderName);
        card.setExpiryDate(YearMonth.now().plusYears(3));
        card.setCardType(Card.CardType.DEBIT);
        card.setStatus(Card.CardStatus.ACTIVE);
        card.setCardNetwork("VISA");
        card.setIsDefault(isDefault);
        card.setDailyLimit(new BigDecimal("2000.00"));
        card.setMonthlyLimit(new BigDecimal("10000.00"));
        card.setCurrentDailySpent(BigDecimal.ZERO);
        card.setCurrentMonthlySpent(BigDecimal.ZERO);
        card.setIsInternationalEnabled(true);
        card.setIsContactlessEnabled(true);
        card.setIsOnlineEnabled(true);
        cardRepository.save(card);
    }

    private void seedBeneficiaryIfMissing(User owner, User beneficiaryUser, String beneficiaryAccountNumber) {
        if (beneficiaryRepository.findByUserAndBeneficiaryEmail(owner, beneficiaryUser.getEmail()).isPresent()) {
            return;
        }

        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setUser(owner);
        beneficiary.setBeneficiaryName(beneficiaryUser.getName());
        beneficiary.setBeneficiaryEmail(beneficiaryUser.getEmail());
        beneficiary.setAccountNumber(beneficiaryAccountNumber);
        beneficiary.setRelationship("FRIEND");
        beneficiary.setIsVerified(true);
        beneficiary.setVerificationDate(LocalDateTime.now().minusDays(2));
        beneficiary.setIsActive(true);
        beneficiary.setVerificationToken("seed-verified-" + owner.getId() + "-" + beneficiaryUser.getId());
        beneficiaryRepository.save(beneficiary);
    }

    private void seedSpendingLimitIfMissing(User user, Account account) {
        if (spendingLimitRepository.findByUserAndCategoryAndLimitType(
                user,
                SpendingLimit.TransactionCategory.TRANSFER,
                SpendingLimit.LimitType.MONTHLY).isPresent()) {
            return;
        }

        SpendingLimit limit = new SpendingLimit();
        limit.setUser(user);
        limit.setAccount(account);
        limit.setCategory(SpendingLimit.TransactionCategory.TRANSFER);
        limit.setLimitType(SpendingLimit.LimitType.MONTHLY);
        limit.setLimitAmount(new BigDecimal("4000.00"));
        limit.setCurrentSpent(new BigDecimal("650.00"));
        limit.setIsActive(true);
        limit.updateResetDate();
        spendingLimitRepository.save(limit);
    }

    private void seedLoanIfMissing(User user, Account account) {
        if (!loanRepository.findByUser(user).isEmpty()) {
            return;
        }

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setLinkedAccount(account);
        loan.setLoanNumber("LN-SEED-" + user.getId());
        loan.setPrincipalAmount(new BigDecimal("3000.00"));
        loan.setOutstandingBalance(new BigDecimal("2450.00"));
        loan.setAnnualInterestRate(new BigDecimal("8.50"));
        loan.setLoanTermMonths(24);
        loan.setLoanType(Loan.LoanType.PERSONAL);
        loan.setStatus(Loan.LoanStatus.ACTIVE);
        loan.setLoanPurpose("Home office equipment");
        loan.setDisbursementDate(LocalDateTime.now().minusMonths(2));
        loan.setNextPaymentDueDate(LocalDateTime.now().plusWeeks(2));
        loan.setTotalInterestPaid(new BigDecimal("120.00"));
        loan.setTotalAmountPaid(new BigDecimal("550.00"));
        loan.setMonthlyPayment(new BigDecimal("136.21"));
        loanRepository.save(loan);
    }
}
