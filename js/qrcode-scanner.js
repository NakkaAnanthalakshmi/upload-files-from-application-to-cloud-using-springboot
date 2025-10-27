// This script uses the open source jsQR library for scanning QR codes from camera stream
// You'll need to include jsQR library in your project via CDN in view-certificate.html or download and serve locally

const video = document.getElementById("preview");
const canvasElement = document.createElement("canvas");
const canvas = canvasElement.getContext("2d");
const resultDiv = document.getElementById("result");

let scanning = false;

navigator.mediaDevices.getUserMedia({ video: { facingMode: "environment" } }).then(function(stream) {
    video.srcObject = stream;
    video.setAttribute("playsinline", true);
    video.play();
    scanning = true;
    tick();
    scan();
});

function tick() {
    if (video.readyState === video.HAVE_ENOUGH_DATA) {
        canvasElement.height = video.videoHeight;
        canvasElement.width = video.videoWidth;
        canvas.drawImage(video, 0, 0, canvasElement.width, canvasElement.height);
    }
    requestAnimationFrame(tick);
}

function scan() {
    if (!scanning) return;

    const imageData = canvas.getImageData(0, 0, canvasElement.width, canvasElement.height);
    const code = jsQR(imageData.data, imageData.width, imageData.height, {
        inversionAttempts: "dontInvert",
    });
    if (code) {
        scanning = false;
        video.srcObject.getTracks().forEach(track => track.stop());
        onQrScanned(code.data);
    } else {
        setTimeout(scan, 300);
    }
}

function onQrScanned(data) {
    resultDiv.innerHTML = `<p>QR Code scanned: ${data}</p>
        <button onclick="downloadCertificate('${encodeURIComponent(data)}')">View/Download Certificate</button>`;
}

function downloadCertificate(qrCodeContent) {
    window.open(`http://localhost:8084/api/certificates/download?qrCodeContent=${qrCodeContent}`, '_blank');
}
