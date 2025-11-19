# Frontend Production Deployment Guide

## Overview

This guide covers the steps to build and deploy the Expense Management System frontend for production.

## Prerequisites

- Node.js 18+ and npm installed
- Backend API deployed and accessible
- Production backend URL available

## Configuration

### 1. Update Production API URL

Edit `frontend/.env.production` and update the `VITE_API_URL` with your production backend URL:

```env
VITE_API_URL=https://api.yourdomain.com/api
```

Or for EC2 deployment:

```env
VITE_API_URL=http://your-ec2-instance-ip:8080/api
```

## Building for Production

### 1. Install Dependencies

```bash
cd frontend
npm install
```

### 2. Create Production Build

```bash
npm run build
```

This will:
- Create an optimized production build in the `dist/` directory
- Minify JavaScript and CSS
- Split code into vendor chunks for better caching
- Generate production-ready assets

### Build Output

The build creates the following structure:

```
dist/
├── index.html
├── vite.svg
└── assets/
    ├── react-vendor-[hash].js      (~45 KB)
    ├── mui-vendor-[hash].js        (~308 KB)
    ├── chart-vendor-[hash].js      (~164 KB)
    ├── index-[hash].js             (~304 KB)
    └── index-[hash].css            (~10 KB)
```

## Testing Production Build Locally

Before deploying, test the production build locally:

```bash
npm run preview
```

This starts a local server at `http://localhost:3000/` serving the production build.

**Important:** Ensure your backend is running at the configured API URL for full functionality testing.

## Deployment Options

### Option 1: AWS S3 + CloudFront (Recommended)

1. **Create S3 Bucket**
   ```bash
   aws s3 mb s3://your-expense-app-frontend
   ```

2. **Configure Bucket for Static Website Hosting**
   ```bash
   aws s3 website s3://your-expense-app-frontend --index-document index.html --error-document index.html
   ```

3. **Upload Build Files**
   ```bash
   aws s3 sync dist/ s3://your-expense-app-frontend --delete
   ```

4. **Set Up CloudFront Distribution**
   - Create CloudFront distribution pointing to S3 bucket
   - Configure custom error response: 404 → /index.html (for React Router)
   - Enable HTTPS with ACM certificate

### Option 2: Netlify

1. **Install Netlify CLI**
   ```bash
   npm install -g netlify-cli
   ```

2. **Deploy**
   ```bash
   netlify deploy --prod --dir=dist
   ```

3. **Configure Redirects**
   Create `dist/_redirects`:
   ```
   /*    /index.html   200
   ```

### Option 3: Vercel

1. **Install Vercel CLI**
   ```bash
   npm install -g vercel
   ```

2. **Deploy**
   ```bash
   vercel --prod
   ```

### Option 4: Traditional Web Server (Nginx/Apache)

1. **Copy Build Files**
   ```bash
   scp -r dist/* user@server:/var/www/expense-app/
   ```

2. **Configure Nginx**
   ```nginx
   server {
       listen 80;
       server_name yourdomain.com;
       root /var/www/expense-app;
       index index.html;

       location / {
           try_files $uri $uri/ /index.html;
       }

       # Cache static assets
       location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
           expires 1y;
           add_header Cache-Control "public, immutable";
       }
   }
   ```

## Environment-Specific Builds

### Development
```bash
npm run dev
```
Uses `.env.development` configuration

### Production
```bash
npm run build
```
Uses `.env.production` configuration

## Performance Optimization

The production build includes:

- **Code Splitting**: Separate vendor chunks for React, Material-UI, and Chart.js
- **Minification**: JavaScript and CSS minified with esbuild
- **Tree Shaking**: Unused code eliminated
- **Asset Optimization**: Images and static assets optimized

## Troubleshooting

### Build Fails

1. Clear node_modules and reinstall:
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```

2. Check Node.js version (requires 18+):
   ```bash
   node --version
   ```

### API Connection Issues

1. Verify `VITE_API_URL` in `.env.production`
2. Check CORS configuration on backend
3. Ensure backend is accessible from frontend domain
4. Check browser console for network errors

### Routing Issues (404 on Refresh)

Configure your web server to redirect all routes to `index.html` for React Router to handle client-side routing.

## Security Considerations

1. **HTTPS**: Always use HTTPS in production
2. **CORS**: Configure backend CORS to allow only your frontend domain
3. **Environment Variables**: Never commit sensitive data to `.env` files
4. **Content Security Policy**: Consider adding CSP headers
5. **API Keys**: Never expose API keys in frontend code

## Monitoring

After deployment, monitor:

- Application performance (load times, bundle sizes)
- Error rates (use error tracking service like Sentry)
- User analytics
- API response times

## Rollback Procedure

If issues occur after deployment:

1. Keep previous build artifacts
2. Redeploy previous version
3. Or use version control to rebuild previous commit

## Next Steps

After successful deployment:

1. Set up CI/CD pipeline for automated deployments
2. Configure monitoring and alerting
3. Set up automated backups
4. Implement blue-green deployment strategy
5. Add performance monitoring tools
