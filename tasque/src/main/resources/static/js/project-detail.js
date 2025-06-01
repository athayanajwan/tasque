// project-detail.js

if (!isAuthenticated()) {
  window.location.href = "login.html";
}

const token = localStorage.getItem("token");
const projectId = new URLSearchParams(window.location.search).get("id");
let currentUserId = null;
let currentProject = null;
let isManager = false;
let ganttInstance = null;

function toDateTimeLocal(isoString) {
  const date = new Date(isoString);
  const pad = (num) => num.toString().padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function formatDateTime(iso) {
  const date = new Date(iso);
  return date.toLocaleString("id-ID", {
    weekday: 'long', year: 'numeric', month: 'long',
    day: 'numeric', hour: '2-digit', minute: '2-digit'
  });
}

function formatRole(role) {
  switch (role) {
    case "PROJECT_MANAGER": return "Project Manager";
    case "MEMBER": return "Anggota";
    default: return role;
  }
}

function renderTaskVisuals(tasks) {
  let todo = 0, inProgress = 0, done = 0;
  tasks.forEach(task => {
    if (task.status === "TO_DO") todo++;
    else if (["IN_PROGRESS", "REVIEW"].includes(task.status)) inProgress++;
    else if (task.status === "COMPLETED") done++;
  });

   console.log('Counts:', {todo, inProgress, done});
  const total = tasks.length;
  const percentDone = total > 0 ? Math.round((done / total) * 100) : 0;

  document.getElementById("todoCount").textContent = todo;
  document.getElementById("inProgressCount").textContent = inProgress;
  document.getElementById("doneCount").textContent = done;
  document.getElementById("progressBar").style.width = `${percentDone}%`;
  document.getElementById("progressBar").textContent = `${percentDone}%`;
  document.getElementById("progressText").textContent = `${percentDone}% tugas selesai`;

  const ganttTasks = [];
  if (currentProject?.start && currentProject?.deadline) {
    ganttTasks.push({
      id: `project-${currentProject.id}`,
      name: `[PROYEK] ${currentProject.name}`,
      start: new Date(currentProject.start).toISOString(),
      end: new Date(currentProject.deadline).toISOString(),
      progress: percentDone,
      custom_class: "bar-project"
    });
  }

  ganttTasks.push(...tasks.filter(task => task.deadline).map(task => {
    const startDate = new Date(task.start || task.deadline);
    const endDate = new Date(task.deadline || task.start);
    return {
      id: task.id,
      name: task.title || "Untitled",
      start: startDate.toISOString(),
      end: endDate.toISOString(),
      progress: task.status === "COMPLETED" ? 100 : task.status === "IN_PROGRESS" ? 50 : task.status === "REVIEW" ? 75 : 0,
      custom_class: "bar-" + (task.status?.toLowerCase() || "")
    };
  }));

  document.getElementById("gantt").innerHTML = "";
  ganttInstance = new Gantt("#gantt", ganttTasks, {
    on_date_change: (task, start, end) => {
      if (task.id.startsWith("project-")) return;
      fetch(`http://localhost:8080/api/tasks/${task.id}`, {
        method: "PUT",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ ...task, deadline: end.toISOString() })
      })
      .then(res => res.ok ? alert("Tanggal tugas diperbarui") : Promise.reject("Gagal update tanggal"))
      .catch(err => alert("Gagal update tanggal: " + err));
    }
  });
}

function loadMembers(members) {
  const list = document.getElementById("memberList");
  list.innerHTML = "";
  members.forEach(member => {
    const li = document.createElement("li");
    li.classList.add("list-group-item");
    li.innerHTML = `<strong>${member.username}</strong> - <em>${formatRole(member.role)}</em>`;
    if (isManager && member.userId !== currentUserId) {
      const removeBtn = document.createElement("button");
      removeBtn.textContent = "Hapus";
      removeBtn.classList.add("btn", "btn-sm", "btn-outline-danger", "ms-2");
      removeBtn.onclick = () => {
        if (confirm(`Hapus ${member.username} dari proyek?`)) {
          fetch(`http://localhost:8080/api/projects/${projectId}/members?username=${encodeURIComponent(member.username)}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` }
          })
          .then(res => res.ok ? location.reload() : res.text().then(msg => Promise.reject(msg)))
          .catch(err => alert("Gagal hapus anggota: " + err));
        }
      };
      li.appendChild(removeBtn);
    }
    list.appendChild(li);
  });
}

function loadTasks(filterTag = "") {
  fetch(`http://localhost:8080/api/tasks?projectId=${projectId}`, {
    headers: { "Authorization": `Bearer ${token}` }
  })
  .then(res => res.json())
  .then(tasks => {
    const taskListDiv = document.getElementById("taskList");
    taskListDiv.innerHTML = "";

    const tagFilter = document.getElementById("tagFilter");
    const uniqueTags = new Set();

    tasks.forEach(task => {
      task.tags?.forEach(tag => uniqueTags.add(tag.name));
    });

    if (tagFilter.options.length <= 1) {
      uniqueTags.forEach(tag => {
        const opt = document.createElement("option");
        opt.value = tag;
        opt.textContent = tag;
        tagFilter.appendChild(opt);
      });
    }

    const filteredTasks = filterTag ? tasks.filter(task => task.tags?.some(tag => tag.name === filterTag)) : tasks;
    renderTaskVisuals(tasks);

    if (filteredTasks.length === 0) {
      taskListDiv.textContent = "Tidak ada tugas.";
      return;
    }

    const ul = document.createElement("ul");
    ul.classList.add("list-group");
    filteredTasks.forEach(task => {
      const li = document.createElement("li");
      li.classList.add("list-group-item", "d-flex", "justify-content-between", "align-items-center");

      const left = document.createElement("div");
      const link = document.createElement("a");
      link.href = `task-detail.html?id=${task.id}`;
      link.textContent = task.title;
      link.classList.add("fw-bold", "text-decoration-none");
      left.appendChild(link);

      const btn = document.createElement("button");
      btn.textContent = "Hapus";
      btn.classList.add("btn", "btn-sm", "btn-danger");
      btn.onclick = () => {
        if (confirm("Yakin ingin menghapus tugas ini?")) {
          fetch(`http://localhost:8080/api/tasks/${task.id}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` }
          })
          .then(res => res.ok ? loadTasks(filterTag) : res.text().then(msg => Promise.reject(msg)))
          .catch(err => alert("Gagal menghapus tugas: " + err));
        }
      };

      li.append(left, btn);
      ul.appendChild(li);
    });
    taskListDiv.appendChild(ul);
  })
  .catch(err => {
    document.getElementById("taskList").textContent = "Gagal memuat tugas: " + err.message;
  });
}

document.getElementById("tagFilter").addEventListener("change", function () {
  loadTasks(this.value);
});

document.getElementById("editProjectForm").onsubmit = function (e) {
  e.preventDefault();
  const updated = {
    name: document.getElementById("editName").value,
    description: document.getElementById("editDescription").value,
    deadline: new Date(document.getElementById("editDeadline").value).toISOString()
  };

  fetch(`http://localhost:8080/api/projects/${projectId}`, {
    method: "PUT",
    headers: {
      "Authorization": `Bearer ${token}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify(updated)
  })
  .then(res => res.ok ? location.reload() : res.text().then(msg => Promise.reject(msg)))
  .catch(err => alert("Gagal menyimpan perubahan: " + err));
};

document.getElementById("deleteBtn").addEventListener("click", () => {
  if (!confirm("Yakin ingin menghapus proyek ini?")) return;

  fetch(`http://localhost:8080/api/projects/${projectId}`, {
    method: "DELETE",
    headers: {
      "Authorization": `Bearer ${token}`,
      "Content-Type": "application/json"
    }
  })
  .then(res => res.ok ? window.location.href = "dashboard.html" : res.text().then(msg => Promise.reject(msg)))
  .catch(err => alert("Gagal menghapus proyek: " + err));
});

document.getElementById("inviteForm").onsubmit = function (e) {
  e.preventDefault();
  const username = document.getElementById("inviteUsername").value;
  const role = document.getElementById("inviteRole").value;

  fetch(`http://localhost:8080/api/users/${encodeURIComponent(username)}`, {
    headers: { "Authorization": `Bearer ${token}` }
  })
  .then(res => res.ok ? res.json() : Promise.reject("Pengguna tidak ditemukan"))
  .then(user => {
    fetch(`http://localhost:8080/api/projects/${projectId}/members?username=${encodeURIComponent(user.username)}&role=${role}`, {
      method: "POST",
      headers: { "Authorization": `Bearer ${token}` }
    })
    .then(res => res.ok ? location.reload() : res.text().then(msg => Promise.reject(msg)))
    .catch(err => alert("Gagal menambahkan: " + err));
  })
  .catch(err => alert("Gagal memuat data pengguna: " + err));
};

document.getElementById("createTaskForm").onsubmit = function (e) {
  e.preventDefault();
  const taskData = {
    title: document.getElementById("taskTitle").value,
    description: document.getElementById("taskDescription").value,
    status: document.getElementById("taskStatus").value,
    priority: document.getElementById("taskPriority").value,
    assignedTo: document.getElementById("assignedTo").value,
    deadline: new Date(document.getElementById("deadlineInput").value).toISOString(),
    start: new Date(document.getElementById("startInput").value).toISOString(),
    projectId: projectId,
    tagNames: document.getElementById("taskTags").value.split(",").map(t => t.trim()).filter(t => t)
  };
  console.log(taskData);

  fetch("http://localhost:8080/api/tasks", {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${token}`,
      "Content-Type": "application/json"
    },
    body: JSON.stringify(taskData)
  })
  .then(res => res.ok ? res.json() : res.text().then(msg => Promise.reject(msg)))
  .then(() => {
    alert("Tugas berhasil ditambahkan.");
    document.getElementById("createTaskForm").reset();
    loadTasks();
  })
  .catch(err => alert("Gagal menambahkan tugas: " + err));
};

// Load project & user
fetch("http://localhost:8080/api/users/me", {
  headers: { "Authorization": `Bearer ${token}` }
})
.then(res => res.json())
.then(user => {
  currentUserId = user.id;
  return fetch(`http://localhost:8080/api/projects/${projectId}`, {
    headers: { "Authorization": `Bearer ${token}` }
  });
})
.then(res => res.json())
.then(project => {
  currentProject = project;
  const currentMember = project.members?.find(m => m.userId === currentUserId);
  isManager = currentMember?.role === "PROJECT_MANAGER";

  document.getElementById("projectCard").innerHTML = `
  <div class="project-card">
    <div class="project-header">
      <i class="bi bi-clipboard-check"></i>
      <span>Detail Proyek</span>
    </div>

    <div class="project-deadline-label text-end">Deadline
      <div class="project-deadline-date">
        ${new Date(project.deadline).toLocaleDateString("id-ID", {
          weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'
        })}
      </div>
    </div>

    <div class="project-title">${project.name}</div>
    <div class="project-meta">Oleh, ${project.createdBy} &nbsp;&nbsp;&nbsp; ${formatDateTime(project.start)}</div>
  </div>
`;

document.getElementById("projectDescription").textContent = project.description;

  loadMembers(project.members);
  loadTasks();
})
.catch(err => {
  document.getElementById("projectDetails").textContent = "Gagal memuat data: " + err.message;
});