const jwtToken = localStorage.getItem("jwtToken");
if (!jwtToken || jwtToken.trim() === "") {
    // Redirect to login page
    window.location.href = "login.html";
}
const messageBox = document.getElementById("messageBox");
const messageInput = document.getElementById("messageInput");
const sendBtn = document.getElementById("sendBtn");
const socket = new WebSocket("ws://10.81.50.242:8080/ws/chat?token="+jwtToken);

sendBtn.addEventListener("click", sendMessage);
document.getElementById("logoutBtn").addEventListener("click", () => {

    // Close websocket connection
    if (socket && socket.readyState === WebSocket.OPEN) {
        socket.close();
    }

    // Remove username from localStorage
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("username")

    // Redirect to login page
    window.location.replace("login.html");
});
// send message to server
function sendMessage(){

    const input = document.getElementById("messageInput");

    const message = input.value;

    if(message.trim() === "") return;

    socket.send(message);

    input.value = "";
}

// display message in window
function displayMessage(msg){

    const div=document.createElement("div");
    div.className="message";
    div.textContent=msg;

    messageBox.appendChild(div);

    messageBox.scrollTop = messageBox.scrollHeight;
}

socket.onmessage = function(event) {
    const msg = event.data;
    displayMessage(msg);
};