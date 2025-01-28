import express, { json } from 'express';
import routes from './routes/model.js';

const app = express();
const PORT = 3000;

// Middleware
app.use(json());

// Routes
app.use('/model/', routes);

// Start Server
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
