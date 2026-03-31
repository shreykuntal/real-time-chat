const jwtToken = localStorage.getItem("jwtToken");
if (!jwtToken || jwtToken.trim() === "") {
    // Redirect to login page
    window.location.href = "login.html";
}
localStorage.setItem("activeUsers", JSON.stringify([localStorage.getItem("username")]));
localStorage.setItem("chats", JSON.stringify({
    "GC": [],
}));
let receipent = "GC";
const messageBox = document.getElementById("messageBox");
const sendBtn = document.getElementById("sendBtn");
const socket = new WebSocket("ws://10.81.53.111:8080/ws/chat?token="+jwtToken);
loadActiveUsers();
loadAllUsers();
sendBtn.addEventListener("click", sendMessage);
document.getElementById("logoutBtn").addEventListener("click", () => {
    if (socket && socket.readyState === WebSocket.OPEN) {
        socket.close();
    }
    localStorage.clear();
    window.location.replace("login.html");
});
function sendMessage(){
    const input = document.getElementById("messageInput");
    const message = input.value;
    if(message.trim() === "") return;
    if (socket.readyState === WebSocket.OPEN) {
        socket.send(receipent + "?" + message);
        const chat = JSON.parse(localStorage.getItem("chats"));
        chat[receipent].push(localStorage.getItem("username") + ": " + message);
        localStorage.setItem("chats", JSON.stringify(chat));
        displayMessage(receipent, localStorage.getItem("username")+": "+message);
    }
    input.value = "";
}

function displayMessage(sender, msg){
    if (sender !== receipent) return;
    const div=document.createElement("div");
    div.className="message";
    div.textContent=msg;
    messageBox.appendChild(div);
    messageBox.scrollTop = messageBox.scrollHeight;
}

socket.onmessage = function(event) {
    const data = event.data;
    const type = data.slice(0, 3);
    if (type === "ADD"){
        const chat = JSON.parse(localStorage.getItem("chats")) || {};
        const activeUsers = JSON.parse(localStorage.getItem("activeUsers")) || [];
        const user = data.slice(3);
        chat[user] = [];
        activeUsers.push(user);
        localStorage.setItem("chats", JSON.stringify(chat));
        localStorage.setItem("activeUsers", JSON.stringify(activeUsers));
        loadAllUsers();
        loadActiveUsers();
    }else if (type === "DEL"){
        const activeUsers = JSON.parse(localStorage.getItem("activeUsers")) || {};
        const user = data.slice(3);
        const index = activeUsers.indexOf(user);
        if (index > -1){
            activeUsers.splice(index, 1);
        }
        localStorage.setItem("activeUsers", JSON.stringify(activeUsers));
        loadActiveUsers();
    }else if (type === "DSP"){
        const toShow = data.slice(3);
        const index = toShow.indexOf(":");
        const sender = toShow.slice(0, index);
        const chat = JSON.parse(localStorage.getItem("chats")) || {};
        chat[sender].push(toShow);
        localStorage.setItem("chats", JSON.stringify(chat));
        displayMessage(sender, toShow);
    }else if (type === "GCH"){
        const toShow = data.slice(3);
        const chat = JSON.parse(localStorage.getItem("chats")) || {};
        chat["GC"].push(toShow);
        localStorage.setItem("chats", JSON.stringify(chat));
        displayMessage("GC", toShow);
    }
};

function loadAllUsers(){
    const chat = JSON.parse(localStorage.getItem("chats")) || {};
    const allUsers = Object.keys(chat);
    const dropdown = document.getElementById("allUsersDropdown");
    dropdown.innerHTML = '';
    allUsers.forEach(user => {
        const option = document.createElement("option");
        option.value = user;
        option.textContent = user;
        dropdown.appendChild(option);
    });
    dropdown.value = receipent;
}

function loadActiveUsers(){
    const activeUsers = JSON.parse(localStorage.getItem("activeUsers") || {});
    const dropdown = document.getElementById("activeUsersDropdown");
    dropdown.innerHTML = '';
    activeUsers.forEach(user => {
        const option = document.createElement("option");
        option.value = user;
        option.textContent = user;
        dropdown.appendChild(option);
    });
}

const allUsers = document.getElementById("allUsersDropdown");
const activeUsers = document.getElementById("activeUsersDropdown");
allUsers.onchange = () => {
    receipent = allUsers.value;
    while (messageBox.firstChild){
        messageBox.removeChild(messageBox.firstChild);
    }
    const chat = JSON.parse(localStorage.getItem("chats"))[receipent];
    chat.forEach(msg => {
        displayMessage(receipent, msg);
    });
}