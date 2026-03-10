// Like Button Functionality
let likeCount = 0;
let isLiked = false;

const likeBtn = document.getElementById('likeBtn');
const likeCountDisplay = document.getElementById('likeCount');

// Load like count from localStorage
if (localStorage.getItem('likeCount')) {
    likeCount = parseInt(localStorage.getItem('likeCount'));
    likeCountDisplay.textContent = likeCount;
}

if (localStorage.getItem('isLiked') === 'true') {
    isLiked = true;
    likeBtn.classList.add('liked');
}

likeBtn.addEventListener('click', () => {
    if (isLiked) {
        // Unlike
        likeCount--;
        isLiked = false;
        likeBtn.classList.remove('liked');
    } else {
        // Like
        likeCount++;
        isLiked = true;
        likeBtn.classList.add('liked');
    }
    
    likeCountDisplay.textContent = likeCount;
    
    // Save to localStorage
    localStorage.setItem('likeCount', likeCount);
    localStorage.setItem('isLiked', isLiked);
});

// Hero Image Scroll Animation
const heroImages = document.querySelectorAll('.hero-img');
const heroSection = document.getElementById('heroSection');

window.addEventListener('scroll', () => {
    const scrolled = window.pageYOffset;
    const heroHeight = heroSection.offsetHeight;
    const scrollPercentage = Math.min(scrolled / heroHeight, 1);
    
    heroImages.forEach((img, index) => {
        const speed = index === 0 ? 0.5 : 0.3;
        const rotateSpeed = index === 0 ? 10 : -15;
        
        img.style.setProperty('--scroll-offset', `${scrolled * speed}px`);
        img.style.setProperty('--rotate-offset', `${scrollPercentage * rotateSpeed}deg`);
        img.classList.add('parallax');
        
        // Fade out as we scroll
        const opacity = Math.max(0.15 - (scrollPercentage * 0.15), 0);
        img.style.opacity = opacity;
    });
});

// Gallery Navigation
let currentImageIndex = 0;
const totalImages = 9;

// Image paths from pics folder
const galleryImages = [
    null, // First item is video
    'pics/app3.jpeg',
    'pics/app2.jpeg',
    'pics/app1.jpeg',
    'pics/app4.jpeg',
    'pics/kiosk.png',
    'pics/superAdmin.jpeg',
    'pics/ngo1.jpeg',
    'pics/3d_model_1.jpeg'
];

const prevImageBtn = document.getElementById('prevImage');
const nextImageBtn = document.getElementById('nextImage');
const currentImageContainer = document.getElementById('currentImage');
const dotsContainer = document.getElementById('dotsContainer');

// Create dots
for (let i = 0; i < totalImages; i++) {
    const dot = document.createElement('div');
    dot.classList.add('dot');
    if (i === 0) dot.classList.add('active');
    dot.addEventListener('click', () => goToImage(i));
    dotsContainer.appendChild(dot);
}

const dots = document.querySelectorAll('.dot');

// Navigate to specific image
function goToImage(index) {
    currentImageIndex = index;
    updateGallery();
}

// Previous image
prevImageBtn.addEventListener('click', () => {
    currentImageIndex = (currentImageIndex - 1 + totalImages) % totalImages;
    updateGallery();
});

// Next image
nextImageBtn.addEventListener('click', () => {
    currentImageIndex = (currentImageIndex + 1) % totalImages;
    updateGallery();
});

// Update gallery display
function updateGallery() {
    // Clear current content
    currentImageContainer.innerHTML = '';
    
    if (currentImageIndex === 0) {
        // Show video for first item
        const videoContainer = document.createElement('div');
        videoContainer.className = 'video-container';
        videoContainer.innerHTML = `
            <iframe 
                width="100%" 
                height="450" 
                src="https://www.youtube.com/embed/Bf8QgrtFD9Y" 
                title="YouTube video player" 
                frameborder="0" 
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                allowfullscreen>
            </iframe>
        `;
        currentImageContainer.appendChild(videoContainer);
    } else {
        // Show actual image
        const img = document.createElement('img');
        img.src = galleryImages[currentImageIndex];
        img.alt = `Project image ${currentImageIndex}`;
        img.className = 'gallery-display-image';
        currentImageContainer.appendChild(img);
    }
    
    // Update dots
    dots.forEach((dot, index) => {
        if (index === currentImageIndex) {
            dot.classList.add('active');
        } else {
            dot.classList.remove('active');
        }
    });
}

// Keyboard navigation
document.addEventListener('keydown', (e) => {
    if (e.key === 'ArrowLeft') {
        prevImageBtn.click();
    } else if (e.key === 'ArrowRight') {
        nextImageBtn.click();
    }
});

// Initialize
updateGallery();
