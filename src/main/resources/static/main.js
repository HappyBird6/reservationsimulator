document.addEventListener("DOMContentLoaded", e => {
    const seatContainer = document.getElementById('seat-container');
    let html = '';
    seatContainer.innerHTML += html;

    document.getElementById('reservationButton').addEventListener('click', function () {
        if (inOrder) return;
        let info;
        reserve(userId).then(data => {
            console.log(`${data}`);
            info = JSON.parse(data);
            document.getElementById("reservation-total-count").innerText = info.order;
            document.getElementById("reservation-order").innerText = info.order;
            reservationId = info.reservationId;
            if (info.isPass === 0) {
                // 패스
                setReservation();
            } else {
                startPolling(reservationId);
            }
        })
        inOrder = true;
    })
    document.getElementById('submitButton').addEventListener('click', function () {
        if (!isPass) return;
        submitSeat().then(data => {
            if (data[0] === '0') {
                console.log("예약성공" + data);
                let seats = data.split(';')[1]
                let seatsPos = seats.split(',');
                for (let i = 0; i < seatsPos.length - 1; i++) {
                    let temp = seatsPos[i].split('-');
                    let row = temp[0];
                    let col = temp[1];
                    document.getElementById('reserve-ok').innerHTML += `
                        <div style="
                            display: flex;
                            width: 100px;
                            justify-content: space-between;
                            border-bottom: 1px dotted rgba(0, 0, 0, 0.5);
                            padding-bottom: 3px;"
                            id='reserve-ok-${row}-${col}'>
                            <div class="flexCenter">
                                <span>[${row},${col}]</span>
                            </div>
                            <div style="
                                    border: 1px solid rgba(0, 0, 0, 0.5);
                                    border-radius: 10px;
                                    width: 50px;
                                    height: 30px;
                                    display: flex;
                                    align-items: center;
                                    justify-content: center;
                                "onclick=cancelReservation(${row},${col})>
                                <span>취소</span>
                            </div>
                        </div>
                    `
                }
                // 예약석 클릭 닫기
                isPass = false;
                inOrder = false;
                document.querySelector(".seat-container .cover").classList.remove("disable");
                document.querySelector(".selected-seat").innerHTML = "";
            } else {
                document.querySelector(".selected-seat").innerHTML = "";
                alert('이미 예약된 자리');
                let temp = JSON.parse(data);
                updateSeat(temp);
            }
        })
    })
    document.getElementById('purgeButton').addEventListener('click', function () {
        purgeQueue().then(count => {
            console.log(`>> 큐에서 ${count}개 비워짐 <<`);
            const alert = document.getElementById('purgeButton').querySelector(".alert")
            alert.innerHTML = `<span> >> ${count}개 비워짐 << </span>`;
            alert.classList.remove("disable");
            setTimeout(function () {
                alert.classList.add("disable");
                alert.innerHTML = ``;
            }, 1000)

        })
    })
});

function refreshSeat() {
    if (!isPass) return;
    getSeat().then(data => {
        console.log(data);
        let temp = JSON.parse(data);
        updateSeat(temp);
    })
}
function setReservation() {
    isPass = true;
    clearInterval(intervalId);
    getSeat().then(data => {
        console.log(data);
        let temp = JSON.parse(data);
        updateSeat(temp);
    })
    document.querySelector(".seat-container .cover").classList.add("disable");
}
function updateSeat(temp) {
    let row = temp.length;
    let col = temp[0].length;
    for (let i = 1; i < row; i++) {
        for (let j = 1; j < col; j++) {
            const cell = document.getElementById(`c${i}-${j}`);
            cell.classList.remove('clicked');
            cell.dataset.selected = 'f';
            if (temp[i][j]) {
                cell.classList.remove("closed");
            } else {
                cell.classList.add("closed");
            }
        }
    }
}
function selectCell(x, y) {
    if (!isPass) return;
    const cell = document.getElementById(`c${x}-${y}`);
    if (cell.classList.contains("closed")) return;
    const selectedContainer = document.querySelector(".selected-seat");
    if (cell.dataset.selected === 'f') {
        cell.classList.add('clicked');
        cell.dataset.selected = 't';
        selectedContainer.innerHTML += `<div class='selected-seat-check' id='s${x}-${y}'>${x},${y}</div>`;

    } else if (cell.dataset.selected === 't') {
        cell.classList.remove('clicked');
        cell.dataset.selected = 'f';
        document.getElementById(`s${x}-${y}`).remove();
    }
}
function startPolling(reservationId) {
    console.log("Polling Start ... ");
    intervalId = setInterval(() => refresh(reservationId), refreshInterval);
}
function changeInterval(newInterval) {
    console.log("Polling Interval Changing ... : " + newInterval);
    refreshInterval = newInterval;
    clearInterval(intervalId);
    intervalId = setInterval(() => refresh(reservationId), refreshInterval);
}
const reserve = async function (userId) {
    const url = '/reserve';
    const data = new URLSearchParams();
    data.append('userId', userId);

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: data
        });

        if (!response.ok) {
            throw new Error(`[ERROR] Fetching ${url} : ${response.status}`);
        }
        const d = await response.text();
        return d;
    } catch (error) {
        console.log('[ERROR] Function reserve : ', error);
    }
}
const refresh = async function (reservationId) {
    console.log("[refresh] : /order로 fetching ... ")
    const url = '/order'
    const data = new URLSearchParams();
    data.append('reservationId', reservationId);
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: data
        });

        if (!response.ok) {
            throw new Error(`[ERROR] Fetching ${url} : ${response.status}`);
        }
        const count = await response.text();
        let temp = JSON.parse(count);
        console.log(temp);
        document.getElementById('reservation-total-count').innerText = temp[0];
        document.getElementById('reservation-order').innerText = temp[1];
        if (temp[2] === 0) {
            clearInterval(intervalId);
            setReservation();
        }
        return count;
    } catch (error) {
        console.log('[ERROR] Function getTotalCount : ', error);
    }
}
const purgeQueue = async function () {
    const url = '/purge'
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });

        if (!response.ok) {
            throw new Error(`[ERROR] Fetching ${url} : ${response.status}`);
        }
        const count = await response.text();
        return count;
    } catch (error) {
        console.log('[ERROR] Function purgeQueue : ', error);
    }
}
const getSeat = async function () {
    const url = '/seat'
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });

        if (!response.ok) {
            throw new Error(`[ERROR] Fetching ${url} : ${response.status}`);
        }
        const count = await response.text();
        return count;
    } catch (error) {
        console.log('[ERROR] Function getSeat : ', error);
    }
}
const submitSeat = async function () {
    const url = '/submitSeat'
    const data = new URLSearchParams();
    const seats = document.querySelectorAll(".selected-seat-check");
    let selectedSeat = '';
    for (let i = 0; i < seats.length; i++) {
        let id = seats[i].id.substring(1);
        selectedSeat += id + ",";
    }
    data.append('userId', userId);
    data.append('selectedSeat', selectedSeat);
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: data
        });

        if (!response.ok) {
            throw new Error(`[ERROR] Fetching ${url} : ${response.status}`);
        }
        const count = await response.text();
        return count;
    } catch (error) {
        console.log('[ERROR] Function submitSeat : ', error);
    }
}