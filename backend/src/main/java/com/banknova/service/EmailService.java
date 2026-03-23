package com.banknova.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    private boolean canSendEmail() {
        if (mailSender == null) {
            logger.warn("Email sender is not configured; skipping email delivery.");
            return false;
        }
        return true;
    }

    @Async
    public void sendTransactionNotification(String toEmail, String transactionType,
            String amount, String otherParty) {
        if (!canSendEmail()) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);

            String subject;
            String body;

            if ("SENT".equals(transactionType)) {
                subject = "BankNova - Money Sent Successfully";
                body = String.format(
                        "Hello,\n\n" +
                                "You have successfully sent $%s to %s.\n\n" +
                                "Transaction Details:\n" +
                                "- Amount: $%s\n" +
                                "- Recipient: %s\n" +
                                "- Type: Money Transfer\n\n" +
                                "If you did not authorize this transaction, please contact support immediately.\n\n" +
                                "Best regards,\n" +
                                "BankNova Team",
                        amount, otherParty, amount, otherParty);
            } else {
                subject = "BankNova - Money Received";
                body = String.format(
                        "Hello,\n\n" +
                                "You have received $%s from %s.\n\n" +
                                "Transaction Details:\n" +
                                "- Amount: $%s\n" +
                                "- Sender: %s\n" +
                                "- Type: Money Transfer\n\n" +
                                "Your funds are now available in your wallet.\n\n" +
                                "Best regards,\n" +
                                "BankNova Team",
                        amount, otherParty, amount, otherParty);
            }

            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            logger.info("Transaction notification email sent to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send transaction notification email to: {}", toEmail, e);
            // Don't throw exception to avoid breaking the transaction
        }
    }

    @Async
    public void sendWelcomeEmail(String toEmail, String userName) {
        if (!canSendEmail()) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to BankNova!");
            message.setText(String.format(
                    "Hello %s,\n\n" +
                            "Welcome to BankNova! Your account has been successfully created.\n\n" +
                            "You can now:\n" +
                            "- Send and receive money instantly\n" +
                            "- View your transaction history\n" +
                            "- Track your spending analytics\n\n" +
                            "Get started by logging into your account and exploring our features.\n\n" +
                            "If you have any questions, feel free to contact our support team.\n\n" +
                            "Best regards,\n" +
                            "BankNova Team",
                    userName));

            mailSender.send(message);
            logger.info("Welcome email sent to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send welcome email to: {}", toEmail, e);
        }
    }

    @Async
    public void sendTransferVerificationCode(String toEmail, String otpCode, String userName) {
        if (!canSendEmail()) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("BankNova - Transfer Verification Code");
            message.setText(String.format(
                    "Hello %s,\n\n" +
                            "Your transfer verification code is: %s\n\n" +
                            "This code will expire in 10 minutes.\n\n" +
                            "If you did not initiate a transfer, please ignore this email.\n\n" +
                            "For security reasons, never share this code with anyone.\n\n" +
                            "Best regards,\n" +
                            "BankNova Team",
                    userName, otpCode));

            message.setSubject("BankNova - Transfer Verification Code");
            mailSender.send(message);
            logger.info("Transfer verification code sent to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send transfer verification code to: {}", toEmail, e);
        }
    }

    @Async
    public void sendBeneficiaryVerificationEmail(String toEmail, String verificationToken, String beneficiaryName) {
        if (!canSendEmail()) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("BankNova - Confirm Beneficiary Addition");
            message.setText(String.format(
                    "Hello,\n\n" +
                            "You have been added as a beneficiary on a BankNova account.\n\n" +
                            "To complete verification, use this token: %s\n\n" +
                            "If you did not expect this email, please ignore it.\n\n" +
                            "Best regards,\n" +
                            "BankNova Team",
                    verificationToken));

            mailSender.send(message);
            logger.info("Beneficiary verification email sent to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send beneficiary verification email to: {}", toEmail, e);
        }
    }

    @Async
    public void sendCardActivationEmail(String toEmail, String userName, String cardLast4) {
        if (!canSendEmail()) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("BankNova - Card Added Successfully");
            message.setText(String.format(
                    "Hello %s,\n\n" +
                            "Your card ending in %s has been successfully added to your BankNova account.\n\n" +
                            "You can now use this card for transfers and payments.\n\n" +
                            "Best regards,\n" +
                            "BankNova Team",
                    userName, cardLast4));

            mailSender.send(message);
            logger.info("Card activation email sent to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send card activation email to: {}", toEmail, e);
        }
    }

    @Async
    public void sendLoanApprovalEmail(String toEmail, String userName, String loanNumber, String amount) {
        if (!canSendEmail()) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("BankNova - Loan Approved");
            message.setText(String.format(
                    "Hello %s,\n\n" +
                            "Congratulations! Your loan has been approved.\n\n" +
                            "Loan Details:\n" +
                            "- Loan Number: %s\n" +
                            "- Amount: $%s\n\n" +
                            "The funds have been transferred to your account.\n" +
                            "You can view your loan details and payment schedule in your account.\n\n" +
                            "Best regards,\n" +
                            "BankNova Team",
                    userName, loanNumber, amount));

            mailSender.send(message);
            logger.info("Loan approval email sent to: {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send loan approval email to: {}", toEmail, e);
        }
    }
}
