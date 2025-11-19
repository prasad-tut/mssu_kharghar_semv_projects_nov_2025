# Frontend Production Deployment Checklist

Use this checklist to ensure your frontend is properly configured and ready for production deployment.

## Pre-Deployment Configuration

- [ ] **Update API URL**: Edit `.env.production` with your production backend URL
  ```env
  VITE_API_URL=https://api.yourdomain.com/api
  ```

- [ ] **Verify Backend CORS**: Ensure backend allows requests from your frontend domain

- [ ] **Review Environment Variables**: Check all environment-specific configurations

- [ ] **Update Meta Tags**: Verify `index.html` has correct title, description, and favicon

## Build Process

- [ ] **Install Dependencies**: Run `npm install` to ensure all packages are up to date

- [ ] **Run Linter**: Execute `npm run lint` to check for code issues

- [ ] **Run Tests**: Execute `npm run test` to verify all tests pass

- [ ] **Create Production Build**: Run `npm run build`

- [ ] **Verify Build Output**: Check that `dist/` directory contains all necessary files
  - index.html
  - assets/ directory with JS and CSS files
  - Static assets (images, icons)

## Local Testing

- [ ] **Test Production Build**: Run `npm run preview` and verify:
  - [ ] Application loads without errors
  - [ ] Login functionality works
  - [ ] API calls succeed (backend must be running)
  - [ ] All pages render correctly
  - [ ] Navigation works properly
  - [ ] Forms submit successfully
  - [ ] File uploads work
  - [ ] Charts and visualizations display
  - [ ] Responsive design works on mobile

- [ ] **Check Browser Console**: Verify no errors in browser console

- [ ] **Test Different Browsers**: Verify compatibility with:
  - [ ] Chrome/Edge
  - [ ] Firefox
  - [ ] Safari (if available)

## Performance Verification

- [ ] **Check Bundle Sizes**: Verify build output shows reasonable file sizes
  - React vendor: ~45 KB
  - MUI vendor: ~308 KB
  - Chart vendor: ~164 KB
  - Main bundle: ~304 KB

- [ ] **Verify Code Splitting**: Confirm separate vendor chunks are created

- [ ] **Test Load Time**: Ensure application loads quickly

## Security Checks

- [ ] **HTTPS Configuration**: Ensure deployment will use HTTPS

- [ ] **No Sensitive Data**: Verify no API keys or secrets in frontend code

- [ ] **Content Security Policy**: Consider adding CSP headers

- [ ] **CORS Configuration**: Verify backend CORS settings are restrictive

## Deployment

- [ ] **Choose Deployment Platform**: Select from:
  - [ ] AWS S3 + CloudFront
  - [ ] Netlify
  - [ ] Vercel
  - [ ] Traditional web server (Nginx/Apache)

- [ ] **Upload Build Files**: Deploy `dist/` directory contents

- [ ] **Configure Routing**: Set up redirect rules for React Router
  - All routes should redirect to `index.html`

- [ ] **Set Up Custom Domain**: Configure DNS and SSL certificate

- [ ] **Enable Caching**: Configure cache headers for static assets

## Post-Deployment Verification

- [ ] **Test Production URL**: Visit your production URL and verify:
  - [ ] Application loads correctly
  - [ ] HTTPS is working
  - [ ] All functionality works
  - [ ] API integration works
  - [ ] No console errors

- [ ] **Test on Mobile Devices**: Verify responsive design on actual devices

- [ ] **Test All User Flows**:
  - [ ] User registration
  - [ ] User login
  - [ ] Create expense
  - [ ] Edit expense
  - [ ] Delete expense
  - [ ] Upload receipt
  - [ ] Submit for approval
  - [ ] Manager approval (if applicable)
  - [ ] Generate reports
  - [ ] Export reports

- [ ] **Performance Testing**: Check page load times and responsiveness

- [ ] **Cross-Browser Testing**: Test on multiple browsers

## Monitoring Setup

- [ ] **Error Tracking**: Set up error monitoring (e.g., Sentry)

- [ ] **Analytics**: Configure analytics (e.g., Google Analytics)

- [ ] **Uptime Monitoring**: Set up uptime checks

- [ ] **Performance Monitoring**: Configure performance tracking

## Documentation

- [ ] **Update README**: Document production URL and access instructions

- [ ] **Document Deployment Process**: Keep deployment steps documented

- [ ] **Create Rollback Plan**: Document how to rollback if needed

## Maintenance

- [ ] **Backup Build**: Keep previous build artifacts for rollback

- [ ] **Set Up CI/CD**: Configure automated deployments (optional)

- [ ] **Schedule Updates**: Plan for dependency updates and security patches

## Emergency Contacts

Document key contacts for production issues:

- Backend Team: _______________
- DevOps Team: _______________
- On-Call Engineer: _______________

## Notes

Add any deployment-specific notes or considerations:

---

**Deployment Date**: _______________
**Deployed By**: _______________
**Production URL**: _______________
**Backend API URL**: _______________
