# Login and MO Post Job Test Report

## 1. Test Objective

This test report is used to verify:

1. Whether different user roles can log in successfully.
2. Whether TA and MO users are directed to the correct pages after login.
3. Whether invalid login information is rejected correctly.
4. Whether an MO user can successfully post a job.
5. Whether the posted job can be found in `jobs.json` after submission.

## 2. Test Environment

- Operating System: Windows
- JDK Version: 21
- Tomcat Version: 10.1
- Browser: Edge / Chrome
- Project URL: `http://localhost:8080/TA_Recruitment/login`

## 3. Test Accounts

| Role | Username | Password |
|------|----------|----------|
| TA | ta001 | ta123456 |
| MO | mo001 | mo123456 |

## 4. Test Cases

### 4.1 TA Login Test

**Test Purpose**  
Verify that a TA user can log in successfully.

**Test Steps**
1. Open `http://localhost:8080/TA_Recruitment/login`
2. Enter username: `ta001`
3. Enter password: `ta123456`
4. Click the **Login** button

**Expected Result**
- Login succeeds
- The system recognizes the user as **TA**
- The page redirects to the TA home page

**Actual Result**
- Login succeeds
- The user is recognized as TA
- The page redirects normally

**Conclusion**  
Pass

---

### 4.2 MO Login Test

**Test Purpose**  
Verify that an MO user can log in successfully and be redirected to the MO job posting page.

**Test Steps**
1. Open `http://localhost:8080/TA_Recruitment/login`
2. Enter username: `mo001`
3. Enter password: `mo123456`
4. Click the **Login** button

**Expected Result**
- Login succeeds
- The system recognizes the user as **MO**
- The page redirects directly to `/mo/post-job`

**Actual Result**
- Login succeeds
- The user is recognized as MO
- The page redirects to the MO posting page successfully

**Conclusion**  
Pass

---

### 4.3 Invalid Login Test

**Test Purpose**  
Verify that incorrect login information is rejected.

**Test Steps**
1. Open `http://localhost:8080/TA_Recruitment/login`
2. Enter an incorrect username or password
3. Click the **Login** button

**Expected Result**
- Login fails
- The system stays on the login page
- An error message is displayed

**Actual Result**
- Login fails
- The system does not enter the home page
- The error message is shown correctly

**Conclusion**  
Pass

---

### 4.4 Unauthorized Access Test for MO Post Job Page

**Test Purpose**  
Verify that only MO users can access the post job page.

**Test Steps**
1. Log in with a TA account
2. Try to visit `http://localhost:8080/TA_Recruitment/mo/post-job`

**Expected Result**
- The system denies access to the page

**Actual Result**
- TA cannot use the MO post job function

**Conclusion**  
Pass

---

### 4.5 MO Post Job and Data Persistence Test

**Test Purpose**  
Verify that after an MO submits a new job, the job data is written into `jobs.json`.

**Test Steps**
1. Log in with MO account: `mo001 / mo123456`
2. Enter the MO post job page
3. Fill in the form fields:
    - Title
    - Category
    - Description
    - Requirements
    - Hours
    - Schedule
    - Deadline
4. Click **Post Job**
5. Open the deployed data file:  
   `D:\Tomcat10.1\webapps\TA_Recruitment\data\jobs.json`
6. Search for the job title just submitted

**Expected Result**
- The form is submitted successfully
- A success message is shown
- A new job record is appended in `jobs.json`
- The new record contains:
    - `jobId`
    - `title`
    - `category`
    - `description`
    - `requirements`
    - `hours`
    - `schedule`
    - `deadline`
    - `status`
    - `createdAt`

**Actual Result**
- Job submission succeeds
- The posted job can be found in `jobs.json`
- The system generates `jobId` automatically
- The system writes `status = OPEN`
- The system writes the current submission time into `createdAt`

**Conclusion**  
Pass

## 5. Test Summary

This round of testing verifies that:

- TA users can log in successfully
- MO users can log in successfully
- Invalid login attempts are rejected correctly
- Only MO users can access the post job page
- After an MO posts a job, the corresponding record can be found in `jobs.json`

Overall, the login function and MO post job function work correctly in the tested local version. The login flow, role restriction, and job data persistence are all functioning as expected.