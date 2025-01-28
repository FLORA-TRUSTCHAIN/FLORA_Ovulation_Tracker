import React from 'react'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './Pages/HomePage';
import AboutPage from './Pages/AboutPage';
import FAQPage from './Pages/FAQPage';
import NewsPage from './Pages/NewsPage';
import ContactPage from './Pages/ContactPage';
import RegisterPage from './Pages/RegisterPage';
import LoginPage from './Pages/LoginPage';
import DashboardPage from './Pages/DashboardPage';
import ErrorPage from './Pages/ErrorPage';
import DefaultLayout from './Layouts/DefaultLayout';
import PrivateRoute from './Routes/PrivateRoute';

const MainApplication = () => {
  return (
    <Router basename={process.env.PUBLIC_URL}>
    <Routes>
      <Route path="/" element={<DefaultLayout> <HomePage /> </DefaultLayout>} />
      <Route path="/about" element={<DefaultLayout> <AboutPage /> </DefaultLayout>} />
      <Route path="/faq" element={<DefaultLayout> <FAQPage /> </DefaultLayout>} />
      <Route path="/news" element={<DefaultLayout> <NewsPage /> </DefaultLayout>} />
      <Route path="/contact" element={<DefaultLayout> <ContactPage /> </DefaultLayout>} />
      <Route path="/register" element={<RegisterPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/dashboard" element={
          <PrivateRoute>
            <DashboardPage />
          </PrivateRoute>
        } />
      <Route path="*" element={<ErrorPage />} />
    </Routes>
  </Router>
  )
}

export default MainApplication
