import {futrzakGameService} from "./game-service.js";

//////////////
// Game
let canvas;
let context;

let score = 0;
//////////////

//////////////
// Ball
const ballRadius = 10;
let ballColor = "rgb(0, 155, 215)"
let ballX;
let ballY;
let ballSpeedX = 2;
let ballSpeedY = -2;
const ballSpeedIncrementValue = 0.05;
//////////////

//////////////
// Player paddle
let lives = 2;
const paddleHeight = 10;
const paddleWidth = 75;
let paddleX;
let paddleSpeed = 7;
let rightPressed = false;
let leftPressed = false;
//////////////

//////////////
// Bricks
const brickRowCount = 3;
const brickColumnCount = 9;
const brickWidth = 75;
const brickHeight = 20;
const brickPadding = 15;
const brickOffsetTop = 30;
const brickOffsetLeft = 30;
const bricks = [];
//////////////

document.addEventListener("load", new function () {
    setupGame();
});
document.addEventListener("keydown", keyDownHandler, false);
document.addEventListener("keyup", keyUpHandler, false);
document.addEventListener("mousemove", mouseMoveHandler, false);

function setupGame() {

    canvas = document.getElementById("myCanvas");
    context = canvas.getContext("2d");

    ballX = canvas.width / 2;
    ballY = canvas.height - 30;

    paddleX = (canvas.width - paddleWidth) / 2;

    // Setup bricks
    for (let column = 0; column < brickColumnCount; column++) {
        bricks[column] = [];
        for (let row = 0; row < brickRowCount; row++) {
            bricks[column][row] = {x: 0, y: 0, status: 1};
        }
    }
    //

    draw();
}

function draw() {
    // drawing code

    context.clearRect(0, 0, canvas.width, canvas.height);
    drawBricks();
    drawBall();
    drawPlayerPaddle();
    drawScore();
    drawLives();
    detectBallCollisionWithBricks();
    ballLogic();
    playerLogic();

    ballX += ballSpeedX;
    ballY += ballSpeedY;

    requestAnimationFrame(draw);
}

function drawBall() {
    context.beginPath();
    context.arc(ballX, ballY, ballRadius, 0, Math.PI * 2);
    context.fillStyle = ballColor;
    context.fill();
    context.closePath();
}

function drawPlayerPaddle() {
    context.beginPath();
    context.rect(paddleX, canvas.height - paddleHeight, paddleWidth, paddleHeight);
    context.fillStyle = "#0095DD";
    context.fill();
    context.closePath();
}

function drawBricks() {
    for (let column = 0; column < brickColumnCount; column++) {
        for (let row = 0; row < brickRowCount; row++) {
            if (bricks[column][row].status === 1) {
                const brickX = (column * (brickWidth + brickPadding)) + brickOffsetLeft;
                const brickY = (row * (brickHeight + brickPadding)) + brickOffsetTop;

                bricks[column][row].x = brickX;
                bricks[column][row].y = brickY;
                context.beginPath();
                context.rect(brickX, brickY, brickWidth, brickHeight);
                context.fillStyle = "#0095DD";
                context.fill();
                context.closePath();
            }
        }
    }
}

function ballLogic() {
    if (ballX + ballSpeedX > canvas.width - ballRadius || ballX + ballSpeedX < ballRadius) {
        ballSpeedX = -ballSpeedX;
        changeBallColor();
    }
    if (ballY + ballSpeedY < ballRadius) {
        ballSpeedY = -ballSpeedY;
        changeBallColor();
    } else if (ballY + ballSpeedY > canvas.height - ballRadius) {
        if (ballX > paddleX && ballX < paddleX + paddleWidth) {
            ballSpeedY = -(Math.abs(ballSpeedY) + ballSpeedIncrementValue);
            ballSpeedX = ballSpeedX < 0 ? (ballSpeedX - ballSpeedIncrementValue) : (ballSpeedX + ballSpeedIncrementValue);
        } else {
            lives--;
            if (!lives) {
                alert("GAME OVER");
                document.location.pathname = "/game/thanks-for-playing";
            }
            else {
                ballX = canvas.width / 2;
                ballY = canvas.height - 30;
                ballSpeedX = 2;
                ballSpeedY = -2;
                paddleX = (canvas.width - paddleWidth) / 2;
            }
        }
    }
}

function detectBallCollisionWithBricks() {
    for (let column = 0; column < brickColumnCount; column++) {
        for (let row = 0; row < brickRowCount; row++) {
            const brick = bricks[column][row];
            if (brick.status === 1) {
                if (ballX > brick.x
                    && ballX < brick.x + brickWidth
                    && ballY > brick.y
                    && ballY < brick.y + brickHeight) {
                    ballSpeedY = ballSpeedY < 0 ? -(ballSpeedY - ballSpeedIncrementValue) : -(ballSpeedY + ballSpeedIncrementValue);
                    ballSpeedX = ballSpeedX < 0 ? (ballSpeedX - ballSpeedIncrementValue) : (ballSpeedX + ballSpeedIncrementValue);
                    brick.status = 0;
                    score++;
                    if (score === brickRowCount * brickColumnCount) {
                        const tokenAndGameInfo = retrieveTokenAndGameInfo();
                        const winConfirmationRequest = {
                            token: tokenAndGameInfo.token,
                            game: tokenAndGameInfo.game
                        }
                        futrzakGameService.confirmWin(winConfirmationRequest).then((data) => {
                            console.log(data);
                        });
                        alert("YOU WIN, CONGRATULATIONS!");
                        document.location.pathname = "/game/thanks-for-playing";
                    }
                }
            }
        }
    }
}

function playerLogic() {
    if (rightPressed) {
        paddleX = Math.min(paddleX + paddleSpeed, canvas.width - paddleWidth);
    } else if (leftPressed) {
        paddleX = Math.max(paddleX - paddleSpeed, 0);
    }
}

function drawScore() {
    context.font = "16px Arial";
    context.fillStyle = "#0095DD";
    context.fillText(`Score: ${score}`, 8, 20);
}

function drawLives() {
    context.font = "16px Arial";
    context.fillStyle = "#0095DD";
    context.fillText(`Lives: ${lives}`, canvas.width - 65, 20);
}


function changeBallColor() {
    let r = Math.random() * 255;
    let g = Math.random() * 255;
    let b = Math.random() * 255;
    ballColor = `rgb(${r}, ${g}, ${b})`;
}

function retrieveTokenAndGameInfo() {
    const urlParams = new URLSearchParams(window.location.search);
    let token = urlParams.get("token");
    return {
        token: `${token}`,
        game: "breakout"
    };
}

function keyDownHandler(e) {
    if (e.key === "Right" || e.key === "ArrowRight") {
        rightPressed = true;
    } else if (e.key === "Left" || e.key === "ArrowLeft") {
        leftPressed = true;
    }
}

function keyUpHandler(e) {
    if (e.key === "Right" || e.key === "ArrowRight") {
        rightPressed = false;
    } else if (e.key === "Left" || e.key === "ArrowLeft") {
        leftPressed = false;
    }
}

function mouseMoveHandler(e) {
    const relativeX = e.clientX - canvas.offsetLeft;
    if (relativeX > 0 && relativeX < canvas.width) {
        paddleX = relativeX - paddleWidth / 2;
    }
}

