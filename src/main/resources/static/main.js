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
        const seatList = JSON.parse(data);        
        
        updateSeat(seatList);
    })
}
function setReservation() {
    isPass = true;
    clearInterval(intervalId);
    getSeat().then(data => {
        console.log(data);
        const seatList = JSON.parse(data);        
        
        updateSeat(seatList);
    })
    const t = document.querySelector(".cover");
    t.style.backgroundColor = "rgba(0,200,0,0.2)";
    t.innerHTML = "예 약 중";
}
function updateSeat(seatList) {
    for(var e in seatList){
        var seat = seatList[e];
        document.getElementById(seat.seatId).querySelector('.curNum').innerText = seat.curNum;
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
        const seatList = await response.text();
        return seatList;
    } catch (error) {
        console.log('[ERROR] Function getSeat : ', error);
    }
}

const submitSeat = async function (seatId) {
    if (!isPass) return;
    const url = '/submitSeat'
    const data = new URLSearchParams();
    data.append('seatId', seatId);
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
        const res = await response.text();
        console.log(res);
        const seatList = JSON.parse(res.substring(2,res.length));
        updateSeat(seatList);
        if(res[0]==='1'){
            alert('예약 실패');
        }else if(res[0]==='0'){
            console.log("예약 성공");
            document.getElementById('reserve-ok').innerHTML += 
            `
                <div style="display:flex;">
                    <div>${userId} : ${seatId}</div>
                </div>
            `
            closeReservation();
        }
        return res;
    } catch (error) {
        console.log('[ERROR] Function submitSeat : ', error);
    }
}
const closeReservation = async function(){
    isPass = false;
    inOrder = false;
    const t = document.querySelector(".cover");
    t.style.backgroundColor = "rgba(0,0,0,0.5)";
    t.innerHTML = "예 약 끝";
}

const changeUserId = function(){
    if(isPass || inOrder) {
        console.log("[유저 변경 실패] 예약 진행 중 변경 불가능");
        return;
    }
    let newId = document.getElementById('input_userId').value;
    let orderId = userId;
    userId = newId;
    console.log(`[유저 변경됨] ${orderId} -> ${userId}`);
}