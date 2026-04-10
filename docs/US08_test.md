# US08 + US00 Integration Test Report

Author: Sunshengkai  
Date: 2026-04-09

## 1. Test Objective

This document records the integration testing process for:

- US00: User Login and Role Identification
- US08: Post Job
- Access control between login role and post job page

## 2. Test Environment

- OS: Windows
- JDK: 21
- Tomcat: 10.1
- Browser: Edge / Chrome
- Project Path: `D:\workspace\TAs_system\TA_Recruitment_System`
- Deploy Path: `D:\Tomcat10.1\webapps\TA_Recruitment`

## 3. Test Accounts

### TA Account
- Username: `ta001`
- Password: `ta123456`
- Role: `TA`

### MO Account
- Username: `mo001`
- Password: `mo123456`
- Role: `MO`

## 4. Test Cases

### TC01 - Unauthenticated user accesses post job page

**Steps**
1. Start Tomcat
2. Open browser
3. Visit `/TA_Recruitment/mo/post-job` directly without login

**Expected Result**
User should not access the post job page directly.  
System should redirect to the login page.

**Actual Result**
System redirected to `/login`.

**Status**
Pass

---

### TC02 - TA user accesses post job page

**Steps**
1. Login with TA account
2. Visit `/TA_Recruitment/mo/post-job`

**Expected Result**
TA user should not be allowed to access this page.

**Actual Result**
TA user was blocked from accessing the post job page.

**Status**
Pass

---

### TC03 - MO user accesses post job page

**Steps**
1. Login with MO account
2. Visit `/TA_Recruitment/mo/post-job`

**Expected Result**
MO user should successfully open the post job page.

**Actual Result**
MO user successfully accessed the post job page and the form was displayed.

**Status**
Pass

---

### TC04 - MO submits empty form

**Steps**
1. Login with MO account
2. Open post job page
3. Submit form with empty required fields

**Expected Result**
System should show validation error message.

**Actual Result**
System displayed validation error message and did not save invalid job data.

**Status**
Pass

---

### TC05 - MO submits valid job form

**Steps**
1. Login with MO account
2. Fill in all required fields
3. Submit the form

**Expected Result**
System should show success message and save job data into `jobs.json`.

**Actual Result**
System displayed success message and a new job record was saved into `jobs.json`.

**Status**
Pass

## 5. Test Conclusion

US08 Post Job has been successfully integrated with US00 login and role identification.

The system now supports:
- redirecting unauthenticated users to the login page
- denying TA users from accessing the MO-only post job page
- allowing MO users to access and submit the post job form
- validating required form fields
- saving valid job records into `jobs.json`