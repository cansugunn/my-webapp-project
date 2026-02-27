<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" xml:lang="en">
<head>
    <meta charset="UTF-8">
    <title>TODO List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-..." crossorigin="anonymous">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body { padding-top: 40px; }
        .task-item { display: flex; align-items: center; justify-content: space-between; }
        .task-title { flex-grow: 1; margin-left: 10px; }
        .task-actions i { cursor: pointer; margin-left: 8px; }
    </style>
</head>
<body>
<div class="container">
    <h2 class="mb-4">My TODO List</h2>
    <form id="newTaskForm" class="input-group mb-3">
        <input type="text" id="newTaskTitle" class="form-control" placeholder="Add a new task..." required>
        <button class="btn btn-primary" type="submit">Add</button>
    </form>
    <ul id="tasksList" class="list-group"></ul>
</div>

<script>
    const apiBase = window.location.origin + '/my-webapp-project/api/tasks';

    async function loadTasks() {
        const res = await fetch(apiBase);
        const tasks = await res.json();
        const list = document.getElementById('tasksList');
        list.innerHTML = '';
        tasks.forEach(t => {
            const li = document.createElement('li');
            li.className = 'list-group-item task-item';
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.checked = t.completed;
            checkbox.addEventListener('change', () => toggleCompleted(t.id, checkbox.checked));
            const titleSpan = document.createElement('span');
            titleSpan.textContent = t.title;
            titleSpan.className = 'task-title';
            const actions = document.createElement('span');
            actions.className = 'task-actions';
            const edit = document.createElement('i');
            edit.className = 'bi bi-pencil';
            edit.addEventListener('click', () => editTask(t.id, titleSpan));
            const trash = document.createElement('i');
            trash.className = 'bi bi-trash text-danger';
            trash.addEventListener('click', () => deleteTask(t.id));
            actions.appendChild(edit);
            actions.appendChild(trash);
            li.appendChild(checkbox);
            li.appendChild(titleSpan);
            li.appendChild(actions);
            list.appendChild(li);
        });
    }

    async function addTask(title) {
        await fetch(apiBase, { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({title}) });
        loadTasks();
    }

    async function deleteTask(id) {
        await fetch(apiBase + '/' + id, { method: 'DELETE' });
        loadTasks();
    }

    async function toggleCompleted(id, completed) {
        await fetch(apiBase + '/' + id, { method: 'PUT', headers:{'Content-Type':'application/json'}, body: JSON.stringify({id, completed}) });
        loadTasks();
    }

    function editTask(id, titleSpan) {
        const current = titleSpan.textContent;
        const input = document.createElement('input');
        input.type = 'text';
        input.value = current;
        input.className = 'form-control';
        input.addEventListener('blur', () => finishEdit());
        input.addEventListener('keydown', e => { if (e.key === 'Enter') finishEdit(); });
        titleSpan.replaceWith(input);
        input.focus();
        async function finishEdit() {
            const newVal = input.value.trim();
            if (newVal && newVal !== current) {
                await fetch(apiBase + '/' + id, { method: 'PUT', headers:{'Content-Type':'application/json'}, body: JSON.stringify({id, title: newVal}) });
            }
            input.replaceWith(titleSpan);
            titleSpan.textContent = newVal || current;
            loadTasks();
        }
    }

    document.getElementById('newTaskForm').addEventListener('submit', e => {
        e.preventDefault();
        const input = document.getElementById('newTaskTitle');
        const val = input.value.trim();
        if (val) {
            addTask(val);
            input.value = '';
        }
    });

    loadTasks();
</script>
</body>
</html>
