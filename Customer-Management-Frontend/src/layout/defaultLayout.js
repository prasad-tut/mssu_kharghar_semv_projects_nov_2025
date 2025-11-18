import React from "react";
import { Container, Row, Col } from "react-bootstrap";
import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";
import Footer from "../components/Footer";


export default function DefaultLayout({ children }) {
  return (
    <div className="d-flex flex-column min-vh-100">
      <Navbar />
      <Container fluid className="flex-grow-1">
        <Row>
          <Col md={2} className="bg-light d-none d-md-block vh-100 p-0">
            <Sidebar />
          </Col>
          <Col md={10} xs={12} className="p-3">
            {children}
          </Col>
        </Row>
      </Container>
      <Footer />
    </div>
  );
}