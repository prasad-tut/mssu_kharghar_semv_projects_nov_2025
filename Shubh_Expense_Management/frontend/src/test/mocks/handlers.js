import { http, HttpResponse } from 'msw';

const API_URL = 'http://localhost:8080/api';

// Mock data
const mockUser = {
  id: 1,
  email: 'test@example.com',
  firstName: 'Test',
  lastName: 'User',
  role: 'USER',
};

const mockToken = 'mock-jwt-token-12345';

const mockCategories = [
  { id: 1, name: 'Travel', description: 'Travel expenses' },
  { id: 2, name: 'Meals', description: 'Meal expenses' },
  { id: 3, name: 'Office Supplies', description: 'Office supply expenses' },
];

const mockExpenses = [
  {
    id: 1,
    category: mockCategories[0],
    amount: 150.00,
    expenseDate: '2024-01-15',
    description: 'Flight to conference',
    status: 'DRAFT',
    createdAt: '2024-01-15T10:00:00',
  },
  {
    id: 2,
    category: mockCategories[1],
    amount: 45.50,
    expenseDate: '2024-01-16',
    description: 'Team lunch',
    status: 'SUBMITTED',
    createdAt: '2024-01-16T12:00:00',
  },
];

export const handlers = [
  // Auth endpoints
  http.post(`${API_URL}/auth/register`, async ({ request }) => {
    const body = await request.json();
    return HttpResponse.json({
      token: mockToken,
      user: {
        ...mockUser,
        email: body.email,
        firstName: body.firstName,
        lastName: body.lastName,
      },
    });
  }),

  http.post(`${API_URL}/auth/login`, async ({ request }) => {
    const body = await request.json();
    
    // Simulate failed login
    if (body.email === 'wrong@example.com') {
      return HttpResponse.json(
        { message: 'Invalid credentials' },
        { status: 401 }
      );
    }

    return HttpResponse.json({
      token: mockToken,
      user: mockUser,
    });
  }),

  http.post(`${API_URL}/auth/refresh`, () => {
    return HttpResponse.json({
      token: 'new-mock-jwt-token',
    });
  }),

  // Category endpoints
  http.get(`${API_URL}/categories`, () => {
    return HttpResponse.json(mockCategories);
  }),

  // Expense endpoints
  http.get(`${API_URL}/expenses`, ({ request }) => {
    const url = new URL(request.url);
    const page = parseInt(url.searchParams.get('page') || '0');
    const size = parseInt(url.searchParams.get('size') || '10');

    return HttpResponse.json({
      content: mockExpenses,
      totalElements: mockExpenses.length,
      totalPages: 1,
      number: page,
      size: size,
    });
  }),

  http.get(`${API_URL}/expenses/:id`, ({ params }) => {
    const expense = mockExpenses.find(e => e.id === parseInt(params.id));
    
    if (!expense) {
      return HttpResponse.json(
        { message: 'Expense not found' },
        { status: 404 }
      );
    }

    return HttpResponse.json(expense);
  }),

  http.post(`${API_URL}/expenses`, async ({ request }) => {
    const body = await request.json();
    const newExpense = {
      id: mockExpenses.length + 1,
      ...body,
      category: mockCategories.find(c => c.id === body.categoryId),
      status: 'DRAFT',
      createdAt: new Date().toISOString(),
    };

    return HttpResponse.json(newExpense, { status: 201 });
  }),

  http.put(`${API_URL}/expenses/:id`, async ({ params, request }) => {
    const body = await request.json();
    const expense = mockExpenses.find(e => e.id === parseInt(params.id));

    if (!expense) {
      return HttpResponse.json(
        { message: 'Expense not found' },
        { status: 404 }
      );
    }

    const updatedExpense = {
      ...expense,
      ...body,
      category: mockCategories.find(c => c.id === body.categoryId),
    };

    return HttpResponse.json(updatedExpense);
  }),

  http.delete(`${API_URL}/expenses/:id`, ({ params }) => {
    const expense = mockExpenses.find(e => e.id === parseInt(params.id));

    if (!expense) {
      return HttpResponse.json(
        { message: 'Expense not found' },
        { status: 404 }
      );
    }

    return new HttpResponse(null, { status: 204 });
  }),

  http.post(`${API_URL}/expenses/:id/submit`, ({ params }) => {
    const expense = mockExpenses.find(e => e.id === parseInt(params.id));

    if (!expense) {
      return HttpResponse.json(
        { message: 'Expense not found' },
        { status: 404 }
      );
    }

    return HttpResponse.json({
      ...expense,
      status: 'SUBMITTED',
      submittedAt: new Date().toISOString(),
    });
  }),

  http.get(`${API_URL}/expenses/pending`, () => {
    return HttpResponse.json(
      mockExpenses.filter(e => e.status === 'SUBMITTED')
    );
  }),

  http.post(`${API_URL}/expenses/:id/approve`, async ({ params, request }) => {
    const body = await request.json();
    const expense = mockExpenses.find(e => e.id === parseInt(params.id));

    if (!expense) {
      return HttpResponse.json(
        { message: 'Expense not found' },
        { status: 404 }
      );
    }

    return HttpResponse.json({
      ...expense,
      status: 'APPROVED',
      reviewedAt: new Date().toISOString(),
      reviewNotes: body.reviewNotes,
    });
  }),

  http.post(`${API_URL}/expenses/:id/reject`, async ({ params, request }) => {
    const body = await request.json();
    const expense = mockExpenses.find(e => e.id === parseInt(params.id));

    if (!expense) {
      return HttpResponse.json(
        { message: 'Expense not found' },
        { status: 404 }
      );
    }

    return HttpResponse.json({
      ...expense,
      status: 'REJECTED',
      reviewedAt: new Date().toISOString(),
      reviewNotes: body.reviewNotes,
    });
  }),

  // Receipt endpoints
  http.post(`${API_URL}/receipts`, () => {
    return HttpResponse.json({
      id: 1,
      fileName: 'receipt.jpg',
      fileType: 'image/jpeg',
      fileSize: 12345,
      uploadedAt: new Date().toISOString(),
    }, { status: 201 });
  }),

  http.get(`${API_URL}/receipts/:id`, () => {
    return new HttpResponse(new Blob(['mock file content']), {
      headers: {
        'Content-Type': 'image/jpeg',
      },
    });
  }),

  http.delete(`${API_URL}/receipts/:id`, () => {
    return new HttpResponse(null, { status: 204 });
  }),
];
