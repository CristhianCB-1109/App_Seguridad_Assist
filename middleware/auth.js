  import jwt from 'jsonwebtoken';
  import 'dotenv/config';

  const JWT_SECRET = process.env.JWT_SECRET;

  const authMiddleware = (req, res, next) => {
    const token = req.header('x-auth-token');

    if (!token) {
      return res.status(401).json({ msg: 'No token, autorización denegada' });
    }

    try {
      const decoded = jwt.verify(token, JWT_SECRET);
      req.user = decoded.user;
      next();
    } catch (e) {
      res.status(401).json({ msg: 'El token no es válido' });
    }
  };

  export default authMiddleware;