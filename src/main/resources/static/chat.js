const username = localStorage.getItem("username");
if (!username || username.trim() === "") {
    // Redirect to login page
    window.location.href = "login.html";
}
const messageBox = document.getElementById("messageBox");
const messageInput = document.getElementById("messageInput");
const sendBtn = document.getElementById("sendBtn");
const socket = new WebSocket("ws://192.168.29.23:8080/ws/chat");

sendBtn.addEventListener("click", sendMessage);
document.getElementById("logoutBtn").addEventListener("click", () => {

    // Close websocket connection
    if (socket && socket.readyState === WebSocket.OPEN) {
        socket.close();
    }

    // Remove username from localStorage
    localStorage.removeItem("username");

    // Redirect to login page
    window.location.replace("login.html");
});
// send message to server
function sendMessage(){

    const username = localStorage.getItem("username");
    const input = document.getElementById("messageInput");

    const message = input.value;

    if(message.trim() === "") return;

    const data = {
        username: username,
        message: message
    };

    socket.send(JSON.stringify(data));

    input.value = "";
}

// display message in window
function displayMessage(user,msg){

    const div=document.createElement("div");
    div.className="message";
    div.textContent=user + ": " + msg;

    messageBox.appendChild(div);

    messageBox.scrollTop = messageBox.scrollHeight;
}

socket.onmessage = function(event) {
    const msg = JSON.parse(event.data);
    displayMessage(msg.username, msg.message);
};