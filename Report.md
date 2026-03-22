## 1. Fact-gathering techniques

In this project, fact gathering was a crucial step in ensuring accurate requirements. By employing various fact-gathering techniques, the team was able to gain a comprehensive understanding of the system requirements from multiple perspectives. Below are some core fact-gathering techniques and their specific application in this project:

### 1.1 Background Reading

Background reading typically refers to reading existing literature, market research reports, competitive analyses, and related documents. This method helps in understanding industry trends, competitor products, and the application of existing technologies.

In the early stages of system development, the team conducted in-depth research into existing systems, technical architectures, and market trends in the recruitment management field. For example, by reading white papers and industry reports on recruitment management systems both domestically and internationally, the team was able to identify existing technological gaps and user needs in the market, thus providing a theoretical basis for the requirements analysis of the new system. In practice, the team analyzed the recruitment management systems of several major competitors (such as linkedin.com and passport.lagou.com) to identify shortcomings in existing products and discover commonly concerned functional requirements, such as the accuracy of resume screening and job matching algorithms. This provided valuable reference for defining the functional requirements of this project.

### 1.2 Interviewing

During the interviews, the team designed specific questions for different user groups. For example, for recruitment managers, the focus was on the difficulties they encountered when screening resumes and posting job information. For marketing TAs, the discussion explored their desire to simplify the application process and improve job matching.

Interviews are a form of in-depth communication, typically used to gather detailed information about a specific interviewee (such as a stakeholder or end-user). It is a highly flexible technique suitable for gathering information with complex contexts or high-level implications. The project team interviewed relevant personnel, teaching assistants (TAs), management teams (MOs), and other potential users at BUPT International School to gain a deeper understanding of their needs and pain points when using the recruitment system. The interviews also helped the team clarify the school's expectations for advanced features such as automated screening and job recommendations.

### 1.3 Questionnaires

Questionnaire surveys are a quick way to collect feedback from a large number of users, and are suitable for scenarios that require gathering a wide range of opinions or understanding the needs of a large number of users.

To understand the experience of BUPT International School students and applicants when using the recruitment system, the team designed questionnaires for students and applicants. These questionnaires investigated their needs and feedback on aspects such as resume submission, job application, and interview scheduling. In practice, the team collected feedback from students and applicants through an online questionnaire. The survey results showed that most students wanted to be able to quickly search for positions matching their majors and interests within the system, and desired a simplified resume submission process. Based on this feedback, the team decided to optimize the job filtering function and streamline the application process. The questionnaire also revealed time conflicts encountered by applicants in interview scheduling, prompting the team to consider adding a smarter automatic interview time coordination function.

### 1.4 Focus Groups

Focus groups involve organizing a small group of representative users for discussion, gathering their perspectives and opinions on specific issues. This method is particularly suitable for exploring specific features or needs and obtaining diverse feedback from multiple user perspectives.

In the early stages of development, the project team organized a focus group, inviting recruiters and applicants from different positions to discuss the functional requirements of the recruitment system, especially modules such as resume screening, job matching, and interview scheduling. In practice, during a focus group discussion, the team presented an initial prototype design and collected feedback from various participants. Recruiters expressed a desire for the system to automatically analyze key skills in resumes and match suitable positions based on those skills. Applicants, on the other hand, wanted to be able to view the status of all their job applications on a single platform and receive timely interview notifications. The focus group feedback helped the team further optimize the feature design, ensuring the system meets the needs of diverse users.

### 1.5 Document Analysis

Document analysis involves a detailed review of existing documents, records, and reports to identify information needs or deficiencies in the existing system. This method is particularly suitable for extracting requirements from historical records and using them as a basis for system design.The team conducted a detailed analysis of the recruitment documents currently used by the school (such as job descriptions, applicant resumes, interview feedback forms, etc.). By analyzing the field settings, information flow, and approval processes in these documents, the team was able to identify missing or inconsistent information in the existing system and processes.

### 1.6 Observation

Observation refers to gathering requirements by personally observing users' behavior in their natural work environment. This method is particularly suitable for understanding the difficulties users encounter when actually using the system and the needs that have not been made explicit.By observing how recruitment staff at BUPT International School use the existing recruitment system (e.g manually screening resumes and recording interview results using Excel spreadsheets), the team was able to identify inefficiencies in the existing system and further optimize the user experience.

In practice, due to a combination of factors such as missing materials and small sample sizes, document analysis and observation methods did not achieve the expected results, therefore they were placed last.

## 2. Iteration plan

This summary is based on the Iteration number in the product backlog, covering the core objectives, selected user stories, workload estimates, key time nodes, and core deliverable of the four iterations, clearly presenting the development focus and iteration connection logic of each stage.

### Iteration 1: Implementation of core MVP basic functions

Build the core framework of the minimum viable product (MVP) of the system, implement the basic operation process of teaching assistants (TA) and module owners (MO), provide functional support for subsequent iterations, and all functions are of must priority.

Start: March 8, 2026 | Completion: March 22, 2026

Core deliverables：

• TA side: core modules of file management, resume upload, job search, and job application;

• MO side: core module of job posting;

• Basic data linkage mechanism (file - resume - application - position);

• Iteration 1 Test Report + Deployable MVP Prototype;

Complete the development of core front-end features in all subsequent iterations.

### Iteration 2: Core process improvement + AI basic function implementation

Complete the core business processes of TA and MO, realize key functions such as application status tracking and candidate review, implement AI-based enhancement functions with priority, and improve the efficiency of core recruitment processes. It relies entirely on all the features of Iteration 1 and requires development based on the published job postings, submitted applications, and uploaded resumes/files.

Start: March 23, 2026 | Completion: April 12, 2026

Core deliverables：

• TA side: real-time tracking of application status, automatic resume filling file function; 

• MO side: centralized candidate review, skill matching score viewing, application status management module; 

• The first AI basic functions (resume analysis, skill matching); 

• The core recruitment process (release - application - review - status update) is implemented in a closed loop; 

Iteration 2 Test Report + System Function Iteration Prototype.

### Iteration 3: Implementation of administrator functions + process optimization

The addition of a core administrator user role addresses management pain points such as workload allocation and scheduling conflicts, implements Must's basic administrator functions and Should's process optimization functions, and ensures the rational allocation of recruitment resources. Based on the functional results of Iteration 1 + Iteration 2, load statistics and conflict detection need to be carried out based on the assigned TA positions and the approved application data.

Start Date: April 13, 2026 | Completion Date: May 3, 2026

Core deliverables：

• Administrator side: Core modules for TA workload statistics and scheduling conflict/overload detection; 

• TA side: Optimized job collection function to improve user experience; 

• System resource management mechanism implemented to achieve reasonable allocation of recruitment work and risk warning; 

Iteration 3: Integrated version of the system with test report and administrator functions.

### Iteration 4: Advanced AI features + Experience optimization + Notification mechanism

Implement advanced features that could be prioritized, including AI-powered intelligent recommendations and multi-role message notifications, to complete the final refinement of the system's user experience and add features, thereby achieving comprehensive product functionality improvement. Relying on the full functional data from Iteration 1 + Iteration 2 + Iteration 3, intelligent recommendations and notification triggers need to be carried out based on TA skills, workload, application status, and job information.

May 4, 2026 | Completed: May 24, 2026

Core deliverables：

• Administrator side: AI-powered intelligent TA recommendation function to improve resource allocation efficiency; 

• Message notification and to-do reminder mechanism for all roles (TA/MO/administrator); 

• System AI function upgrade (from basic analysis/matching to advanced intelligent recommendation); 

• Full-function closed loop implementation of the product, completing the final experience refinement; 

Iteration 4 test report + final official version of the system (including all functions).

## 3. Prioritization methods

In this project, prioritization methods are crucial for ensuring features are implemented in order of importance and resource availability. We used the following methods to prioritize user stories in the Product Backlog: 

### 3.1 MoSCoW Method 

The MoSCoW method is a commonly used prioritization technique in Agile development. It categorizes user stories into four different categories to ensure that the most important features are implemented first, given limited time and resources. The specific categories are as follows: Must Have: Features that must be implemented. If these features are not implemented, the system will not function properly. For example, "creating user profiles" and "uploading resumes" in a recruitment system are basic functions that must be completed before system delivery. Should Have: Important but deferable features. The absence of these features will not affect the core operation of the system, but they are crucial to the system's improvement. For example, the "resume screening function" is crucial for improving work efficiency, but its implementation can be postponed if time is tight. Could Have: Features that can be added to improve the user experience, but do not directly affect the core functionality of the system. For example, the "job recommendation system" adds intelligence to the system, but it is not essential. 

### 3.2 Prioritization based on business value

The priority of each user story is also evaluated based on its value to the business. This approach focuses on assessing the return on investment (ROI) of each feature, i.e., whether the implementation of the feature can bring significant business benefits or cost savings. For example, the "automatic job matching" feature in the recruitment system improves recruitment efficiency and reduces the time spent on manual screening, so it is considered to have high business value, while "custom job application forms," although somewhat usable, have relatively low value. 

### 3.3 Risk Assessment Method 

In the prioritization process, the team also considered risk factors, i.e., the technical difficulties or unforeseen obstacles that may be encountered in implementing certain features. Low-risk, high-return features are prioritized. For example, the resume upload function, although technically simple, may cause the entire system to malfunction if its development is delayed, so it is given priority. Some complex AI features, such as "automatic skills gap detection," are classified as features for later versions due to their high technical difficulty and long implementation time. 

### 3.4 User Feedback 

In addition to the above methods, the team also collected feedback from BUPT International School staff and potential users (such as recruiters, students, etc.). Through focus groups and questionnaires, the team understood the needs and expectations of all parties regarding the system. By analyzing user needs and feedback, the team adjusted priorities accordingly. For example, students and applicants paid particular attention to the application status check and job recommendation functions, so these two functions were given higher priority.

## 4. Estimation methods

In this project, the team used several estimation methods to predict the workload and development time required for user stories. The specific estimation methods are as follows:

### 4.1 Story Points Estimating: 

Story point estimation is the primary estimation method we use. It quantifies workload by assigning story points to each user story. Each story point represents the relative effort, complexity, and uncertainty required to complete the task. The team determines the appropriate story points by discussing the complexity and workload of each user story.

The Fibonacci sequence (1, 2, 3, 5, 8, 13, 21) is used to assign story points to each user story, with more complex tasks receiving a larger number of story points. For example, the resume upload function, due to its simplicity, is estimated to have 3 story points, while more complex functions such as automatic job matching are assigned 5 story points.

In our table, the team estimated story points for each functional task. For example, the job application function was assigned 5 story points because it is relatively complex and involves permission management for different user roles, while the job search function was assigned 3 story points, with a lower expected development workload.

### 4.2 Hours Estimating

Hour estimation is a straightforward estimation method where teams predict the required man-hours based on the actual amount of work each task will have. This approach is particularly useful for tasks that are already well-defined and of relatively low complexity.

Teams use hourly estimates to handle specific development tasks, especially when functional modules are clearly defined and task details are complete. For example, implementing resume uploads is estimated at 16 hours, while designing the job listing page is estimated at 8 hours. These estimates, based on actual development time predictions, help teams allocate resources more accurately.

The table lists the estimated time-frames for each user story. For example, creating a user profile for a TA applicant is estimated to take 8 hours, while the job posting feature is estimated to take 12 hours. This granular estimation allows the team to better manage the development process and ensure on-time delivery of each module.

### 4.3 Prioritization and estimation combined

In this project, we not only considered the technical complexity of the tasks, but also adjusted the workload estimates based on the priority of each function. By combining the MoSCoW prioritization method, the team prioritized each user story, thereby allocating the estimated time reasonably.

Higher-priority features (such as resume upload and job application functions) are assigned higher estimated values, while lower-priority features (such as skills matching functions) are scheduled for later development. This allows the team to flexibly adjust based on priority and workload, ensuring that the most important features are developed in a timely manner.

In the table, the resume upload function and the job application function are listed as high-priority tasks and are assigned relatively high time estimates, while later functions (such as workload balancing) are assigned to lower priority and have relatively lower time estimates.