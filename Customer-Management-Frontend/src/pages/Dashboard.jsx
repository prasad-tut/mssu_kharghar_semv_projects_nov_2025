import React from "react";
import { Container, Row, Col, Card } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";

export default function Dashboard() {
  return (
    <Container fluid className="py-4">
      <h2 className="fw-semibold mb-4">Dashboard Overview</h2>

      {/* Stats Cards */}
      <Row className="g-4">
        <Col md={3} sm={6}>
          <Card className="shadow-sm border-0 text-center">
            <Card.Body>
              <i className="bi bi-people display-6 text-primary"></i>
              <h5 className="mt-3">Total Customers</h5>
              <h3 className="fw-bold">1,245</h3>
            </Card.Body>
          </Card>
        </Col>

        <Col md={3} sm={6}>
          <Card className="shadow-sm border-0 text-center">
            <Card.Body>
              <i className="bi bi-person-plus display-6 text-success"></i>
              <h5 className="mt-3">New Customers</h5>
              <h3 className="fw-bold">85</h3>
            </Card.Body>
          </Card>
        </Col>

        <Col md={3} sm={6}>
          <Card className="shadow-sm border-0 text-center">
            <Card.Body>
              <i className="bi bi-telephone display-6 text-warning"></i>
              <h5 className="mt-3">Active Contacts</h5>
              <h3 className="fw-bold">312</h3>
            </Card.Body>
          </Card>
        </Col>

        <Col md={3} sm={6}>
          <Card className="shadow-sm border-0 text-center">
            <Card.Body>
              <i className="bi bi-graph-up display-6 text-danger"></i>
              <h5 className="mt-3">Growth Rate</h5>
              <h3 className="fw-bold">+12%</h3>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Recent Activity Section */}
      {/* <Card className="mt-5 shadow-sm border-0">
        <Card.Header className="fw-semibold">Recent Customer Activity</Card.Header>
        <Card.Body>
          <ul className="list-group list-group-flush">
            <li className="list-group-item">✅ New customer added — John Doe</li>
            <li className="list-group-item">🕓 Customer details updated — Jane Smith</li>
            <li className="list-group-item">📞 Contacted — David Johnson</li>
            <li className="list-group-item">🗑️ Customer deleted — Sarah Lee</li>
          </ul>
        </Card.Body>
      </Card> */}
    </Container>
  );
}
