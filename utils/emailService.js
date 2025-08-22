// C:\fluter-proyect\vacaciones\my_first_app\functions\api\utils\emailService.js
import nodemailer from 'nodemailer';

// Configuración del transporter (ejemplo con Gmail, CAMBIA ESTO PARA PRODUCCIÓN)
// Para Gmail, necesitarás habilitar "Less secure app access" o usar "App passwords" si tienes 2FA.
// Es mejor usar un servicio transaccional como SendGrid, Mailgun, etc.
const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: process.env.EMAIL_USER,    // Tu email de envío (ej: tu_email@gmail.com)
        pass: process.env.EMAIL_PASSWORD // Tu contraseña o "App Password" de Gmail
    }
});

// Función para enviar el email de restablecimiento de contraseña
export const sendPasswordResetEmail = async (toEmail, resetUrl) => {
    const mailOptions = {
        from: process.env.EMAIL_USER, // Remitente
        to: toEmail,                  // Destinatario
        subject: 'Restablecimiento de Contraseña para tu cuenta PideYa',
        html: `
            <p>Hola,</p>
            <p>Has solicitado restablecer tu contraseña para tu cuenta de PideYa.</p>
            <p>Haz clic en el siguiente enlace para restablecer tu contraseña:</p>
            <p><a href="${resetUrl}">${resetUrl}</a></p>
            <p>Este enlace expirará en 1 hora.</p>
            <p>Si no solicitaste un restablecimiento de contraseña, ignora este correo electrónico.</p>
            <p>Gracias,</p>
            <p>El equipo de PideYa</p>
        `,
    };

    try {
        await transporter.sendMail(mailOptions);
        console.log(`Email de restablecimiento enviado a ${toEmail}`);
    } catch (error) {
        console.error(`Error al enviar email de restablecimiento a ${toEmail}:`, error);
        throw new Error('No se pudo enviar el email de restablecimiento de contraseña.');
    }
};