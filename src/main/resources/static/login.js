function login() {

    let username = document.getElementById("username").value;
    let password = document.getElementById("password").value;

    fetch("/", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                localStorage.setItem("username", username);
                window.location.href = "/chat.html";
            } else {
                document.getElementById("message").innerText = data.message;
            }
        });
}
