package com.foodiego.foodiego.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Forgot Password OTP
    public void sendOtp(String email, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);

        message.setSubject(
                "FoodieGo Password Reset OTP");

        message.setText(
                "Your OTP is: " + otp +
                        "\n\nValid for 10 minutes.");

        mailSender.send(message);
    }

    // Welcome Email
    public void sendWelcomeEmail(String email, String name) {

    try {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);

        helper.setSubject("🎉 Welcome to FoodieGo");

        String html = """
                <!DOCTYPE html>
                <html>
                <body style="
                    margin:0;
                    padding:0;
                    background:#0f172a;
                    font-family:Arial,sans-serif;
                ">

                <div style="
                    max-width:600px;
                    margin:40px auto;
                    background:#111827;
                    border-radius:20px;
                    overflow:hidden;
                    box-shadow:0 10px 30px rgba(0,0,0,.4);
                ">

                    <div style="
                        background:linear-gradient(135deg,#ff6b00,#ff9900);
                        padding:40px;
                        text-align:center;
                    ">
                        <h1 style="
                            color:white;
                            margin:0;
                            font-size:36px;
                        ">
                            🍔 FoodieGo
                        </h1>

                        <p style="
                            color:white;
                            margin-top:10px;
                        ">
                            India's Fastest Food Delivery Platform
                        </p>
                    </div>

                    <div style="padding:40px;color:#f3f4f6;">

                        <h2>
                            Welcome """ + name + """
                        </h2>

                        <p>
                            Your account has been successfully created.
                        </p>

                        <p>
                            Start exploring restaurants, ordering delicious meals,
                            applying coupons, and tracking deliveries in real time.
                        </p>

                        <div style="
                            margin:30px 0;
                            padding:20px;
                            background:#1f2937;
                            border-radius:12px;
                        ">
                            ✅ Browse Restaurants<br><br>
                            ✅ Order Food Online<br><br>
                            ✅ Track Orders Live<br><br>
                            ✅ Exclusive Offers & Coupons
                        </div>

                        <div style="text-align:center;">

                            <a href="http://localhost:8080/login"
                               style="
                               display:inline-block;
                               background:#ff6b00;
                               color:white;
                               text-decoration:none;
                               padding:14px 30px;
                               border-radius:10px;
                               font-weight:bold;
                            ">
                               Start Ordering
                            </a>

                        </div>

                    </div>

                    <div style="
                        background:#0b1220;
                        color:#9ca3af;
                        text-align:center;
                        padding:20px;
                        font-size:13px;
                    ">
                        Thank you for choosing FoodieGo ❤️
                    </div>

                </div>

                </body>
                </html>
                """;

        helper.setText(html, true);

        mailSender.send(message);

    } catch (MessagingException e) {
        e.printStackTrace();
    }
}
}