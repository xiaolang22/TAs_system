<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Post Job</title>
</head>
<body>
<h2>Post a Job</h2>

<p style="color: green;">
    ${param.success eq 'true' ? 'Job posted successfully!' : ''}
</p>

<p style="color: red;">
    ${errorMsg}
</p>

<form method="post" action="${pageContext.request.contextPath}/mo/post-job">
    <div>
        <label>Title:</label>
        <input type="text" name="title" value="${job.title}">
    </div>

    <div>
        <label>Category:</label>
        <select name="category">
            <option value="">Please select</option>
            <option value="TA">TA</option>
            <option value="Invigilator">Invigilator</option>
        </select>
    </div>

    <div>
        <label>Description:</label><br>
        <textarea name="description" rows="5" cols="40">${job.description}</textarea>
    </div>

    <div>
        <label>Requirements:</label><br>
        <textarea name="requirements" rows="5" cols="40">${job.requirements}</textarea>
    </div>

    <div>
        <label>Hours:</label>
        <input type="text" name="hours" value="${job.hours}">
    </div>

    <div>
        <label>Schedule:</label>
        <input type="text" name="schedule" value="${job.schedule}">
    </div>

    <div>
        <label>Deadline:</label>
        <input type="date" name="deadline" value="${job.deadline}">
    </div>

    <div style="margin-top: 10px;">
        <button type="submit">Post Job</button>
    </div>
</form>
</body>
</html>