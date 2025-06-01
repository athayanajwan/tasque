// task-ui.js
const token = localStorage.getItem("token");
const urlParams = new URLSearchParams(window.location.search);
const taskId = urlParams.get("id");
let isManager = false;
let projectId = null;
let currentUserId = null;
let isAssignedTo = false;

if (!token || !taskId) {
  document.getElementById("taskDescription").textContent = "Token atau ID tidak ditemukan.";
} else {
  fetch("http://localhost:8080/api/users/me", {
    headers: { "Authorization": "Bearer " + token }
  })
    .then(res => res.json())
    .then(user => {
      currentUserId = user.id;
      return fetch(`http://localhost:8080/api/tasks/${taskId}`, {
        headers: { "Authorization": "Bearer " + token }
      });
    })
    .then(res => res.json())
    .then(task => {
      projectId = task.project.id;
      isAssignedTo = task.assignedTo?.id === currentUserId;
      updateTaskView(task);
      renderComments(task.comments, currentUserId, isManager);
      return fetch(`http://localhost:8080/api/projects/${projectId}`, {
        headers: { "Authorization": "Bearer " + token }
      });
    })
    .then(res => res.json())
    .then(project => {
      const member = project.members.find(m => m.userId === currentUserId);
      isManager = member?.role === "PROJECT_MANAGER";
      
      
      if (isManager || isAssignedTo) {
        showEditForm(isManager, assignedTo);
      }
      updateBackToProjectLink();
    })
    .catch(err => {
      document.getElementById("taskDescription").textContent = "Gagal memuat: " + err.message;
    });
}

function updateTaskView(task) {
  document.getElementById("taskTitle").textContent = task.title;
  document.getElementById("taskDescription").textContent = task.description || "-";
  document.getElementById("taskStatus").textContent = task.status;
  document.getElementById("taskPriority").textContent = task.priority;

  const start = task.start?.split("T") || ["", ""];
  const deadline = task.deadline?.split("T") || ["", ""];

  document.getElementById("taskStartDate").value = start[0];
  document.getElementById("taskStartTime").value = start[1]?.slice(0, 5);
  document.getElementById("taskDeadlineDate").value = deadline[0];
  document.getElementById("taskDeadlineTime").value = deadline[1]?.slice(0, 5);

  document.getElementById("taskAssignedTo").value = task.assignedTo?.name || "-";

  const tagsContainer = document.getElementById("taskTags");
tagsContainer.innerHTML = "";
if (task.tags && task.tags.length > 0) {
  task.tags.forEach(tag => {
    const span = document.createElement("span");
    span.className = "tag-badge";
    span.textContent = `${tag.name}`;
    tagsContainer.appendChild(span);
  });
} else {
  tagsContainer.textContent = "-";
}

  // Sync with form
  document.getElementById("title").value = task.title;
  document.getElementById("description").value = task.description || "";
  document.getElementById("assignedTo").value = task.assignedTo?.name || "";
  document.getElementById("status").value = task.status;
  document.getElementById("priority").value = task.priority;
  document.getElementById("start").value = task.start ? task.start.slice(0, 16) : "";
  document.getElementById("deadline").value = task.deadline ? task.deadline.slice(0, 16) : "";
  document.getElementById("tags").value = task.tags?.map(t => t.name).join(", ") || "";
}

function showEditForm(manager, assignedTo) {
  document.getElementById("taskEdit").style.display = "block";
  if (!manager) {
    ["title", "description", "assignedTo", "priority", "start", "deadline", "tags"].forEach(id => {
      document.getElementById(id).disabled = true;
    });
  }
  if (!assignedTo){
     ["title", "description", "assignedTo", "priority", "start", "deadline", "tags", "status"].forEach(id => {
      document.getElementById(id).disabled = true;
    }); 
  }
  document.getElementById("editTaskForm").addEventListener("submit", function (e) {
    e.preventDefault();
    const updatedData = {
      title: document.getElementById("title").value,
      description: document.getElementById("description").value,
      status: document.getElementById("status").value,
      priority: document.getElementById("priority").value,
      assignedTo: document.getElementById("assignedTo").value,
      start: document.getElementById("start").value,
      deadline: document.getElementById("deadline").value,
      projectId: projectId,
      tagNames: document.getElementById("tags").value.split(",").map(t => t.trim()).filter(Boolean)
    };

    fetch(`http://localhost:8080/api/tasks/${taskId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + token
      },
      body: JSON.stringify(updatedData)
    })
    .then(res => {
      if (!res.ok) throw new Error("Gagal mengupdate tugas.");
      return res.json();
    })
    .then(() => {
      alert("Tugas berhasil diperbarui!");
      location.reload();
    })
    .catch(err => alert("Error: " + err.message));
  });
}

function renderComments(comments, currentUserId, isManager) {
  const list = document.getElementById("commentList");
  list.innerHTML = comments?.length ? "" : "<p>Belum ada komentar.</p>";
  comments.forEach(comment => {
    const div = document.createElement("div");
    div.style.borderBottom = "1px solid #ddd";
    div.style.marginBottom = "10px";
    div.style.padding = "5px 0";
    const authorRole = comment.author.role ? ` (${comment.author.role})` : "";
    const deleteBtn = (comment.author.id === currentUserId || isManager)
      ? `<button onclick=\"deleteComment('${comment.id}')\">Hapus</button>` : "";
    div.innerHTML = `
      <p><strong>${comment.author.name}${authorRole}</strong> <small>${comment.createdAt}</small></p>
      <p>${comment.content}</p>
      ${deleteBtn}
    `;
    list.appendChild(div);
  });
}

function addComment() {
  const content = document.getElementById("newComment").value.trim();
  if (!content) return alert("Komentar tidak boleh kosong.");
  fetch(`http://localhost:8080/api/tasks/${taskId}/comments?userId=${currentUserId}`, {
    method: "POST",
    headers: {
      "Authorization": "Bearer " + token,
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ content })
  })
    .then(res => {
      if (!res.ok) throw new Error("Gagal menambahkan komentar.");
      return res.json();
    })
    .then(() => {
      document.getElementById("newComment").value = "";
      return loadTaskAndComments();
    })
    .catch(err => alert("Error: " + err.message));
}

function deleteComment(commentId) {
  if (!confirm("Hapus komentar ini?")) return;
  fetch(`http://localhost:8080/api/tasks/${taskId}/comments/${commentId}?userId=${currentUserId}`, {
    method: "DELETE",
    headers: {
      "Authorization": "Bearer " + token
    }
  })
    .then(res => {
      if (!res.ok) throw new Error("Gagal menghapus komentar.");
      return loadTaskAndComments();
    })
    .catch(err => alert("Error: " + err.message));
}

function loadTaskAndComments() {
fetch(`http://localhost:8080/api/tasks/${taskId}`, {
    headers: { "Authorization": "Bearer " + token }
  })
  .then(res => res.json())
  .then(task => {
    updateTaskView(task);
    renderComments(task.comments, currentUserId, isManager);
  })
  .catch(err => {
    alert("Gagal memuat ulang data tugas: " + err.message);
  });
}

