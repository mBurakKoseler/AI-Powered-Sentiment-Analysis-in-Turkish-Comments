// Global variables
let currentProductId = null;
let products = [];
let categoryKeywords = null;

// Theme management
let currentTheme = localStorage.getItem('theme') || 'light';

// Search functionality
let allProducts = [];
let filteredProducts = [];
let searchQuery = '';

// API Base URL
const API_BASE_URL = 'http://localhost:8081/api';

// Load Keywords from JSON
async function loadKeywords() {
    try {
        const response = await fetch('/keywords.json');
        if (!response.ok) {
            throw new Error('Failed to load keywords');
        }
        categoryKeywords = await response.json();
        console.log('Keywords loaded successfully:', categoryKeywords);
    } catch (error) {
        console.error('Error loading keywords:', error);
        // Fallback to hardcoded keywords
        categoryKeywords = {
            categories: {
                quality_durability: { name: 'Kalite & Dayanıklılık' },
                usage_performance: { name: 'Kullanım & Performans' },
                service_delivery: { name: 'Hizmet & Teslimat' }
            }
        };
    }
}

// Get Category Labels from JSON
function getCategoryLabels() {
    if (!categoryKeywords || !categoryKeywords.categories) {
        // Fallback labels
        return {
            'quality_durability': 'Kalite & Dayanıklılık',
            'usage_performance': 'Kullanım & Performans',
            'service_delivery': 'Hizmet & Teslimat',
            'shipping': 'Hizmet & Teslimat', // Geriye uyumluluk
            'quality': 'Kalite & Dayanıklılık', // Geriye uyumluluk
            'performance': 'Kullanım & Performans', // Geriye uyumluluk
            'general': 'Genel'
        };
    }
    
    const labels = {};
    Object.keys(categoryKeywords.categories).forEach(key => {
        labels[key] = categoryKeywords.categories[key].name;
    });
    
    // Geriye uyumluluk için eski etiketler
    labels['shipping'] = labels['service_delivery'] || 'Hizmet & Teslimat';
    labels['quality'] = labels['quality_durability'] || 'Kalite & Dayanıklılık';
    labels['performance'] = labels['usage_performance'] || 'Kullanım & Performans';
    labels['general'] = 'Genel';
    
    return labels;
}

// Event Listeners
document.addEventListener('DOMContentLoaded', function() {
    // Initialize theme
    initializeTheme();
    
    // Load keywords first, then other data
    loadKeywords().then(() => {
        // Load initial data
        loadProducts();
        
        // Setup all event listeners
        setupEventListeners();
        setupModals();
        setupStarRating();
        setupCharacterCounter();
        setupMobileNavigation();
        setupScrollEffects();
        setupSmoothScrolling();
        
        // Add scroll to top button
        addScrollToTopButton();
        
        // Add interactive effects
        addInteractiveEffects();
    });
});

// Setup Event Listeners
function setupEventListeners() {
    // Review form submission
    const reviewForm = document.getElementById('reviewForm');
    if (reviewForm) {
        reviewForm.addEventListener('submit', handleReviewSubmit);
    }

    // Add product form submission
    const addProductForm = document.getElementById('addProductForm');
    if (addProductForm) {
        addProductForm.addEventListener('submit', handleAddProductSubmit);
    }

    // Bulk review form submission
    const bulkReviewForm = document.getElementById('bulkReviewForm');
    if (bulkReviewForm) {
        bulkReviewForm.addEventListener('submit', handleBulkReviewSubmit);
    }
    

}

// Setup Modals with modern handling
function setupModals() {
    const modals = document.querySelectorAll('.modal');
    const closeButtons = document.querySelectorAll('.modal-close');

    // Close modal when clicking on X
    closeButtons.forEach(button => {
        button.addEventListener('click', function() {
            const modal = this.closest('.modal');
            closeModal(modal.id);
        });
    });

    // Close modal when clicking outside
    modals.forEach(modal => {
        modal.addEventListener('click', function(e) {
            if (e.target === this) {
                closeModal(this.id);
            }
        });
    });

    // Close modal with Escape key
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            const openModal = document.querySelector('.modal[style*="display: block"]');
            if (openModal) {
                closeModal(openModal.id);
            }
        }
    });
}

// Setup Star Rating with enhanced interaction
function setupStarRating() {
    const starContainers = document.querySelectorAll('.star-rating');
    
    starContainers.forEach(container => {
        const stars = container.querySelectorAll('label');
        const inputs = container.querySelectorAll('input');
        
        stars.forEach((star, index) => {
            // Hover effects
            star.addEventListener('mouseenter', function() {
                resetStars(container);
                highlightStars(container, index);
            });
            
            star.addEventListener('mouseleave', function() {
                resetStars(container);
                const checkedInput = container.querySelector('input:checked');
                if (checkedInput) {
                    highlightStars(container, parseInt(checkedInput.value) - 1);
                }
            });
            
            // Click to select
            star.addEventListener('click', function() {
                const input = inputs[index];
                input.checked = true;
                updateRatingText(container, index + 1);
                highlightStars(container, index);
            });
        });
    });
}

// Setup Character Counter for textareas
function setupCharacterCounter() {
    const textareas = document.querySelectorAll('textarea');
    
    textareas.forEach(textarea => {
        const counter = textarea.parentElement.querySelector('.textarea-counter span');
        if (counter) {
            textarea.addEventListener('input', function() {
                const count = this.value.length;
                counter.textContent = count;
                
                // Change color based on character count
                if (count > 450) {
                    counter.style.color = '#ef4444';
                } else if (count > 400) {
                    counter.style.color = '#f59e0b';
                } else {
                    counter.style.color = '#6b7280';
                }
            });
        }
    });
}

// Setup Mobile Navigation
function setupMobileNavigation() {
    const navToggle = document.querySelector('.nav-toggle');
    const navMenu = document.querySelector('.nav-menu');
    
    if (navToggle && navMenu) {
        navToggle.addEventListener('click', function() {
            navMenu.classList.toggle('active');
            navToggle.classList.toggle('active');
        });
    }
}

// Enhanced Scroll Effects
function setupScrollEffects() {
    // Navbar background on scroll
    window.addEventListener('scroll', function() {
        const navbar = document.querySelector('.navbar');
        if (window.scrollY > 100) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
        
        // Show/hide scroll to top button
        const scrollTopBtn = document.querySelector('.scroll-top');
        if (scrollTopBtn) {
            if (window.scrollY > 300) {
                scrollTopBtn.classList.add('show');
            } else {
                scrollTopBtn.classList.remove('show');
            }
        }
        
        // Parallax effect for background shapes
        const shapes = document.querySelectorAll('.bg-shape');
        shapes.forEach((shape, index) => {
            const speed = 0.5 + (index * 0.1);
            const yPos = -(window.scrollY * speed);
            shape.style.transform = `translateY(${yPos}px)`;
        });
    });
    
    // Enhanced intersection observer for animations
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-in');
                
                // Add staggered animation for product cards
                if (entry.target.classList.contains('product-card')) {
                    const cards = document.querySelectorAll('.product-card');
                    const index = Array.from(cards).indexOf(entry.target);
                    entry.target.style.animationDelay = `${index * 0.1}s`;
                }
            }
        });
    }, observerOptions);
    
    // Observe elements for animation
    document.querySelectorAll('.product-card, .analytics-card-detail, .metric-card').forEach(el => {
        observer.observe(el);
    });
}

// Setup Smooth Scrolling
function setupSmoothScrolling() {
    const navLinks = document.querySelectorAll('.nav-link');
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            const href = this.getAttribute('href');
            if (href.startsWith('#')) {
                e.preventDefault();
                const targetId = href.substring(1);
                const targetSection = document.getElementById(targetId);
                if (targetSection) {
                    targetSection.scrollIntoView({ 
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            }
        });
    });
}

// Add Enhanced Scroll to Top Button
function addScrollToTopButton() {
    const scrollTopBtn = document.createElement('button');
    scrollTopBtn.className = 'scroll-top';
    scrollTopBtn.innerHTML = '<i class="fas fa-arrow-up"></i>';
    scrollTopBtn.style.background = 'var(--gradient-primary)';
    scrollTopBtn.style.backdropFilter = 'blur(10px)';
    scrollTopBtn.style.border = '1px solid rgba(255, 255, 255, 0.2)';
    
    scrollTopBtn.addEventListener('click', function() {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
        
        // Add click animation
        this.style.transform = 'scale(0.9)';
        setTimeout(() => {
            this.style.transform = 'scale(1)';
        }, 150);
    });
    
    document.body.appendChild(scrollTopBtn);
}

// Add Interactive Hover Effects
function addInteractiveEffects() {
    // Add hover effects to buttons
    document.addEventListener('mouseover', function(e) {
        if (e.target.classList.contains('btn') || e.target.closest('.btn')) {
            const btn = e.target.classList.contains('btn') ? e.target : e.target.closest('.btn');
            btn.style.transform = 'translateY(-2px)';
        }
    });
    
    document.addEventListener('mouseout', function(e) {
        if (e.target.classList.contains('btn') || e.target.closest('.btn')) {
            const btn = e.target.classList.contains('btn') ? e.target : e.target.closest('.btn');
            btn.style.transform = 'translateY(0)';
        }
    });
    
    // Add ripple effect to buttons
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('btn') || e.target.closest('.btn')) {
            const btn = e.target.classList.contains('btn') ? e.target : e.target.closest('.btn');
            const ripple = document.createElement('span');
            const rect = btn.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            const x = e.clientX - rect.left - size / 2;
            const y = e.clientY - rect.top - size / 2;
            
            ripple.style.width = ripple.style.height = size + 'px';
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';
            ripple.classList.add('ripple');
            
            btn.appendChild(ripple);
            
            setTimeout(() => {
                ripple.remove();
            }, 600);
        }
    });
}

// Theme Management Functions
function initializeTheme() {
    document.documentElement.setAttribute('data-theme', currentTheme);
    updateThemeIcon();
}

function toggleTheme() {
    currentTheme = currentTheme === 'light' ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', currentTheme);
    localStorage.setItem('theme', currentTheme);
    updateThemeIcon();
}

function updateThemeIcon() {
    const themeIcon = document.getElementById('theme-icon');
    if (themeIcon) {
        themeIcon.className = currentTheme === 'light' ? 'fas fa-moon' : 'fas fa-sun';
    }
}

// Helper Functions
function resetStars(container) {
    container.querySelectorAll('label').forEach(star => {
        star.classList.remove('selected', 'hover');
    });
}

function highlightStars(container, index) {
    const stars = container.querySelectorAll('label');
    for (let i = 0; i <= index; i++) {
        stars[i].classList.add('selected');
    }
}

function updateRatingText(container, rating) {
    const ratingText = container.parentElement.querySelector('.rating-text');
    if (ratingText) {
        const texts = ['', 'Çok Kötü', 'Kötü', 'Orta', 'İyi', 'Mükemmel'];
        ratingText.textContent = texts[rating] || 'Puanınızı seçin';
    }
}

// Modal Functions
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'block';
        document.body.style.overflow = 'hidden';
        
        // Focus first input in modal
        const firstInput = modal.querySelector('input, textarea, select');
        if (firstInput) {
            setTimeout(() => firstInput.focus(), 100);
        }
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';
        document.body.style.overflow = 'auto';
        
        // Reset form if exists
        const form = modal.querySelector('form');
        if (form) {
            form.reset();
            resetStars(form.querySelector('.star-rating'));
        }
    }
}

// Open specific modals
function openProductModal() {
    openModal('addProductModal');
}

function openBulkReviewModal() {
    openModal('bulkReviewModal');
    loadProductsForDropdown();
}

function openReviewModal(productId) {
    currentProductId = productId;
    const form = document.getElementById('reviewForm');
    if (form) {
        form.setAttribute('data-product-id', productId);
    }
    openModal('reviewModal');
}

// Scroll to products section
function scrollToProducts() {
    const productsSection = document.getElementById('products');
    if (productsSection) {
        productsSection.scrollIntoView({ 
            behavior: 'smooth',
            block: 'start'
        });
    }
}

// Load Products
async function loadProducts() {
    try {
        const response = await fetch(`${API_BASE_URL}/products`);
        const products = await response.json();
        
        // Store all products for search functionality
        allProducts = products;
        filteredProducts = products;
        
        displayProducts(products);
    } catch (error) {
        console.error('Error loading products:', error);
        showNotification('Ürünler yüklenirken hata oluştu: ' + error.message, 'error');
    }
}

// Display Products
function displayProducts(products) {
    const productsGrid = document.getElementById('productsGrid');
    if (!productsGrid) return;

    productsGrid.innerHTML = '';

    if (products.length === 0) {
        if (searchQuery) {
            productsGrid.innerHTML = `
                <div class="no-results">
                    <i class="fas fa-search"></i>
                    <h3>Arama sonucu bulunamadı</h3>
                    <p>"${searchQuery}" için herhangi bir ürün bulunamadı.</p>
                </div>
            `;
        } else {
            productsGrid.innerHTML = '<p class="no-products">Henüz ürün bulunmamaktadır.</p>';
        }
        return;
    }

    products.forEach(product => {
        const productCard = document.createElement('div');
        productCard.innerHTML = createProductCard(product);
        
        // Add click event to product card (excluding buttons)
        const cardElement = productCard.querySelector('.product-card');
        cardElement.onclick = (event) => {
            // Don't open product detail if clicking on buttons or their children
            if (event.target.closest('.product-actions') || event.target.closest('button')) {
                return;
            }
            showProductDetail(product.id);
        };
        
        productsGrid.appendChild(productCard);
    });
}

// Create Product Card
function createProductCard(product) {
    const stars = '★'.repeat(Math.round(product.averageRating || 0)) + '☆'.repeat(5 - Math.round(product.averageRating || 0));
    const hybridScore = product.hybridScore || 0;
    const reviewCount = product.totalReviews || 0;
    
    // Show 0 scores if no reviews
    const displayRating = reviewCount === 0 ? 0 : (product.averageRating || 0);
    const displayHybridScore = reviewCount === 0 ? 0 : (hybridScore * 5);
    
    // Highlight search terms
    const highlightedName = highlightSearchTerms(product.name, searchQuery);
    const highlightedDescription = highlightSearchTerms(product.description, searchQuery);
    
    // Get category icon
    const categoryIcon = getCategoryIcon(product.category);
    
    return `
        <div class="product-card">
            <div class="product-image">
                <i class="${categoryIcon}"></i>
            </div>
            <div class="product-info">
                <h3>${highlightedName}</h3>
                <p>${highlightedDescription}</p>
                <div class="product-price">₺${product.price}</div>
                
                <div class="product-scores">
                    <div class="score-item">
                        <span class="score-label">Puanlama</span>
                        <span class="score-value">${displayRating.toFixed(1)}</span>
                    </div>
                    <div class="score-item">
                        <span class="score-label">Hibrit</span>
                        <span class="score-value hybrid-score">${displayHybridScore.toFixed(1)}/5</span>
                    </div>
                    <div class="score-item">
                        <span class="score-label">Yorumlar</span>
                        <span class="score-value">${reviewCount}</span>
                    </div>
                </div>
                
                <div class="product-actions">
                    <button class="btn btn-secondary glass-btn" onclick="event.stopPropagation(); showReviews(${product.id}, '${product.name}')">
                        <i class="fas fa-eye"></i>
                        <span>Yorumlar</span>
                    </button>
                    <button class="btn btn-primary glass-btn" onclick="event.stopPropagation(); openReviewModal(${product.id})">
                        <i class="fas fa-comment"></i>
                        <span>Yorum Ekle</span>
                    </button>
                </div>
            </div>
        </div>
    `;
}

// Get category icon based on product category
function getCategoryIcon(category) {
    const iconMap = {
        'Electronics': 'fas fa-microchip',
        'Fashion': 'fas fa-tshirt',
        'Home & Living': 'fas fa-home',
        'Sports': 'fas fa-dumbbell',
        'Books': 'fas fa-book',
        'Beauty': 'fas fa-palette',
        'Elektronik': 'fas fa-microchip',
        'Giyim': 'fas fa-tshirt',
        'Ev & Yaşam': 'fas fa-home',
        'Spor': 'fas fa-dumbbell',
        'Kitap': 'fas fa-book',
        'Kozmetik': 'fas fa-palette'
    };
    return iconMap[category] || 'fas fa-cube';
}

// Enhanced Search Functions
function performSearch() {
    const searchInput = document.getElementById('searchInput');
    const searchClear = document.querySelector('.search-clear');
    
    searchQuery = searchInput.value.trim().toLowerCase();
    
    // Show/hide clear button with animation
    if (searchQuery) {
        searchClear.style.display = 'block';
        searchClear.style.opacity = '0';
        setTimeout(() => {
            searchClear.style.opacity = '1';
        }, 100);
    } else {
        searchClear.style.opacity = '0';
        setTimeout(() => {
            searchClear.style.display = 'none';
        }, 200);
    }
    
    if (searchQuery) {
        // Enhanced search in product names, descriptions, and categories
        filteredProducts = allProducts.filter(product => {
            const nameMatch = product.name.toLowerCase().includes(searchQuery);
            const descriptionMatch = product.description.toLowerCase().includes(searchQuery);
            const categoryMatch = (product.category || '').toLowerCase().includes(searchQuery);
            
            return nameMatch || descriptionMatch || categoryMatch;
        });
        
        // Add search animation
        const productsGrid = document.getElementById('productsGrid');
        if (productsGrid) {
            productsGrid.style.opacity = '0.5';
            setTimeout(() => {
                productsGrid.style.opacity = '1';
            }, 150);
        }
    } else {
        filteredProducts = allProducts;
    }
    
    displayProducts(filteredProducts);
    updateSearchResultsInfo();
}

function clearSearch() {
    const searchInput = document.getElementById('searchInput');
    const searchClear = document.querySelector('.search-clear');
    
    searchInput.value = '';
    searchQuery = '';
    searchClear.style.display = 'none';
    
    filteredProducts = allProducts;
    displayProducts(filteredProducts);
    updateSearchResultsInfo();
}

function highlightSearchTerms(text, query) {
    if (!query || !text) return text;
    
    const regex = new RegExp(`(${query})`, 'gi');
    return text.replace(regex, '<span class="search-highlight">$1</span>');
}

function updateSearchResultsInfo() {
    // This function can be used to show search results info if needed
    // For now, we'll keep it simple
}

// Category Reviews Function
async function showCategoryReviews(productId, category, categoryName) {
    try {
        const reviews = await loadReviews(productId);
        
        // Filter reviews by category
        const categoryReviews = reviews.filter(review => {
            // Map category names to review categories
            const categoryMap = {
                'quality_durability': ['quality', 'quality_durability'],
                'usage_performance': ['performance', 'usage_performance'],
                'service_delivery': ['shipping', 'service_delivery']
            };
            
            const reviewCategory = review.category || 'general';
            return categoryMap[category]?.includes(reviewCategory) || false;
        });
        
        if (categoryReviews.length === 0) {
            showNotification(`${categoryName} kategorisinde henüz yorum bulunmuyor.`, 'info');
            return;
        }
        
        // Display filtered reviews in a modal
        displayCategoryReviewsModal(productId, categoryName, categoryReviews);
        openModal('categoryReviewsModal');
        
    } catch (error) {
        console.error('Error loading category reviews:', error);
        showNotification('Kategori yorumları yüklenirken hata oluştu.', 'error');
    }
}


// Display Category Reviews Modal
function displayCategoryReviewsModal(productId, categoryName, reviews) {
    const modalContent = document.getElementById('categoryReviewsModalContent');
    
    modalContent.innerHTML = `
        <div class="category-reviews-header">
            <h2>${categoryName} - Yorumlar</h2>
            <div class="category-reviews-summary">
                <span class="review-count">${reviews.length} yorum</span>
                <div class="category-badge">
                    <i class="fas fa-tag"></i>
                    <span>${categoryName}</span>
                </div>
            </div>
        </div>
        
        <div class="category-reviews-container">
            ${reviews.map(review => createDetailedReviewItem(review)).join('')}
        </div>
    `;
}

// Show Product Detail
async function showProductDetail(productId) {
    try {
        const response = await fetch(`${API_BASE_URL}/products/${productId}`);
        if (!response.ok) {
            throw new Error('Product not found');
        }
        
        const product = await response.json();
        const reviews = await loadReviews(productId);
        
        displayProductDetail(product, reviews);
        openModal('productModal');
        
    } catch (error) {
        console.error('Error loading product detail:', error);
        showNotification('Ürün detayları yüklenirken hata oluştu.', 'error');
    }
}

// Load Reviews
async function loadReviews(productId) {
    try {
        const response = await fetch(`${API_BASE_URL}/reviews/product/${productId}`);
        if (!response.ok) {
            return [];
        }
        return await response.json();
    } catch (error) {
        console.error('Error loading reviews:', error);
        return [];
    }
}

// Display Product Detail
function displayProductDetail(product, reviews) {
    const productDetail = document.getElementById('productDetail');
    
    const hybridScore = product.hybridScore || 0;
    const averageRating = product.averageRating || 0;
    
    // Kategori puanlarını kullanıcı dostu biçimde göster
    const formatCategoryScore = (score) => {
        if (score === 0 || score === null || score === undefined) {
            return 'ilgili yorum yok';
        }
        return `${(score * 5).toFixed(1)}/5`;
    };
    
    productDetail.innerHTML = `
        <div class="product-detail">
            <div class="product-header">
                <div class="product-header-content">
                    <h2>${product.name}</h2>
                    <p class="product-description">${product.description || 'Açıklama bulunmuyor'}</p>
                    <div class="product-price-large">₺${product.price || 0}</div>
                </div>
                <div class="product-header-actions">
                    <button class="btn btn-danger glass-btn" onclick="deleteProduct(${product.id}, '${product.name}')">
                        <i class="fas fa-trash"></i>
                        Ürünü Sil
                    </button>
                </div>
            </div>
            
            <div class="analytics-section-detail">
                <h3>Analiz Sonuçları</h3>
                <div class="analytics-grid-detail">
                    <div class="analytics-card-detail">
                        <div class="analytics-icon">
                            <i class="fas fa-chart-line"></i>
                        </div>
                        <h4>Hibrit Puanlama</h4>
                        <p>Yıldız puanı ve AI duygu analizinin birleşimi</p>
                        <div class="score-display-detail" id="hybridScoreDetail">${(hybridScore * 5).toFixed(2)}</div>
                    </div>
                    <div class="analytics-card-detail">
                        <div class="analytics-icon">
                            <i class="fas fa-layer-group"></i>
                        </div>
                        <h4>Kategori Analizi</h4>
                        <div class="category-scores-detail">
                            <div class="category-score-detail" onclick="showCategoryReviews(${product.id}, 'quality_durability', 'Kalite & Dayanıklılık')">
                                <span class="category-label">Kalite & Dayanıklılık</span>
                                <span class="category-value">${formatCategoryScore(product.qualityScore)}</span>
                            </div>
                            <div class="category-score-detail" onclick="showCategoryReviews(${product.id}, 'usage_performance', 'Kullanım & Performans')">
                                <span class="category-label">Kullanım & Performans</span>
                                <span class="category-value">${formatCategoryScore(product.performanceScore)}</span>
                            </div>
                            <div class="category-score-detail" onclick="showCategoryReviews(${product.id}, 'service_delivery', 'Hizmet & Teslimat')">
                                <span class="category-label">Hizmet & Teslimat</span>
                                <span class="category-value">${formatCategoryScore(product.shippingScore)}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="reviews-section">
                <div class="reviews-header">
                    <h3>Yorumlar (${reviews.length})</h3>
                    <div class="reviews-actions">
                        <button class="btn btn-primary" onclick="addReview(${product.id})">
                            <i class="fas fa-plus"></i> Yeni Yorum Ekle
                        </button>
                    </div>
                </div>
                
                <div class="reviews-list">
                    ${reviews.length === 0 ? '<p>Henüz yorum bulunmuyor.</p>' : 
                      reviews.map(review => createReviewItem(review)).join('')}
                </div>
            </div>
        </div>
    `;
}

// Create Review Item
function createReviewItem(review) {
    const stars = '★'.repeat(review.starRating) + '☆'.repeat(5 - review.starRating);
    const hybridScore = review.hybridScore || 0;
    const sentimentScore = review.sentimentScore || 0;
    
    return `
        <div class="review-item">
            <div class="review-header">
                <div class="review-stars">${stars}</div>
                <div class="review-meta">
                    ${new Date(review.createdAt).toLocaleDateString('tr-TR')}
                    <button class="btn btn-danger btn-sm glass-btn" onclick="deleteReview(${review.id}, ${review.productId})">
                        <i class="fas fa-trash"></i> Sil
                    </button>
                </div>
            </div>
            <div class="review-content">${review.comment}</div>
            <div class="review-scores">
                <span class="review-score">Hibrit: ${(hybridScore * 5).toFixed(1)}/5</span>
                <span class="review-score">Duygu: ${(sentimentScore * 5).toFixed(1)}/5</span>
            </div>
        </div>
    `;
}

// Show Reviews
window.showReviews = async function(productId, productName) {
    try {
        const reviews = await loadReviews(productId);
        displayReviewsModal(productId, productName, reviews);
        openModal('reviewsModal');
    } catch (error) {
        console.error('Error loading reviews:', error);
        showNotification('Yorumlar yüklenirken hata oluştu.', 'error');
    }
}

// Display Reviews Modal
function displayReviewsModal(productId, productName, reviews) {
    const reviewsModalContent = document.getElementById('reviewsModalContent');
    
    reviewsModalContent.innerHTML = `
        <div class="reviews-modal-header">
            <h2>${productName} - Yorumlar</h2>
            <div class="reviews-summary">
                <span class="review-count">${reviews.length} yorum</span>
                <button class="btn btn-primary" onclick="addReview(${productId})">
                    <i class="fas fa-plus"></i> Yeni Yorum Ekle
                </button>
            </div>
        </div>
        
        <div class="reviews-container">
            ${reviews.length === 0 ? 
                '<div class="no-reviews"><p>Bu ürün için henüz yorum bulunmuyor.</p><button class="btn btn-primary" onclick="addReview(' + productId + ')">İlk Yorumu Siz Yazın</button></div>' : 
                reviews.map(review => createDetailedReviewItem(review)).join('')
            }
        </div>
    `;
}

// Create Detailed Review Item
function createDetailedReviewItem(review) {
    const stars = '★'.repeat(review.starRating) + '☆'.repeat(5 - review.starRating);
    const hybridScore = review.hybridScore || 0;
    const sentimentScore = review.sentimentScore || 0;
    const sentimentLabel = review.sentimentLabel || 'neutral';
    const category = review.category || 'general';
    
    const sentimentColor = sentimentLabel === 'positive' ? '#28a745' : 
                          sentimentLabel === 'negative' ? '#dc3545' : '#6c757d';
    
    const categoryLabels = getCategoryLabels();
    
    return `
        <div class="review-item-detailed">
            <div class="review-header-detailed">
                <div class="review-stars-detailed">
                    <span class="stars">${stars}</span>
                    <span class="rating-text">${review.starRating}/5</span>
                </div>
                <div class="review-meta-detailed">
                    <span class="review-date">${new Date(review.createdAt).toLocaleDateString('tr-TR')}</span>
                    <span class="review-category">${categoryLabels[category] || category}</span>
                    <button class="btn btn-danger btn-sm glass-btn" onclick="deleteReview(${review.id}, ${review.productId})">
                        <i class="fas fa-trash"></i> Sil
                    </button>
                </div>
            </div>
            
            <div class="review-content-detailed">
                <p>${review.comment}</p>
            </div>
            
            <div class="review-scores-detailed">
                <div class="score-item-detailed">
                    <span class="score-label">Hibrit Puan:</span>
                    <span class="score-value">${(hybridScore * 5).toFixed(1)}/5</span>
                </div>
                <div class="score-item-detailed">
                    <span class="score-label">Duygu Analizi:</span>
                    <span class="score-value sentiment-score" style="color: ${sentimentColor}">
                        ${(sentimentScore * 5).toFixed(1)}/5
                    </span>
                </div>
            </div>
        </div>
    `;
}

// Add Review
function addReview(productId) {
    currentProductId = productId;
    const form = document.getElementById('reviewForm');
    if (form) {
        form.setAttribute('data-product-id', productId);
    }
    openModal('reviewModal');
}

// Handle Review Submit
async function handleReviewSubmit(event) {
    event.preventDefault();
    
    const form = event.target;
    const productId = form.getAttribute('data-product-id');
    const starRating = form.querySelector('input[name="starRating"]:checked');
    const comment = form.querySelector('#comment').value.trim();
    
    if (!starRating) {
        showNotification('Lütfen yıldız puanı seçin!', 'warning');
        return;
    }
    
    if (!comment) {
        showNotification('Lütfen yorum yazın!', 'warning');
        return;
    }
    
    if (comment.length > 500) {
        showNotification('Yorum 500 karakterden uzun olamaz!', 'warning');
        return;
    }
    
    // Show loading state
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Gönderiliyor...';
    submitBtn.disabled = true;
    
    try {
        const response = await fetch(`${API_BASE_URL}/reviews`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                productId: parseInt(productId),
                starRating: parseInt(starRating.value),
                comment: comment
            })
        });
        
        if (response.ok) {
            const reviewResponse = await response.json();
            console.log('Review created successfully:', reviewResponse);
            
            // Close modal
            closeModal('reviewModal');
            
            // Show success notification
            showNotification('Yorum başarıyla eklendi!', 'success');
            
            // Refresh product list
            await loadProducts();
            
        } else {
            const errorData = await response.json();
            console.error('Review creation failed:', errorData);
            showNotification('Yorum eklenirken hata oluştu: ' + (errorData.message || 'Bilinmeyen hata'), 'error');
        }
        
    } catch (error) {
        console.error('Review submission error:', error);
        showNotification('Yorum gönderilirken hata oluştu: ' + error.message, 'error');
    } finally {
        // Reset button state
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
    }
}

// Enhanced Notification System
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <i class="fas fa-${getNotificationIcon(type)}"></i>
            <span>${message}</span>
        </div>
        <button class="notification-close" onclick="this.parentElement.remove()">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    // Add glassmorphism effect
    notification.style.background = 'rgba(255, 255, 255, 0.05)';
    notification.style.backdropFilter = 'blur(20px)';
    notification.style.border = '1px solid rgba(255, 255, 255, 0.1)';
    
    // Add to page
    document.body.appendChild(notification);
    
    // Animate in with bounce effect
    setTimeout(() => {
        notification.classList.add('show');
        notification.style.transform = 'translateX(0) scale(1)';
    }, 100);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        notification.classList.remove('show');
        notification.style.transform = 'translateX(100%) scale(0.8)';
        setTimeout(() => notification.remove(), 300);
    }, 5000);
}

function getNotificationIcon(type) {
    switch (type) {
        case 'success': return 'check-circle';
        case 'error': return 'exclamation-circle';
        case 'warning': return 'exclamation-triangle';
        default: return 'info-circle';
    }
}

// Product Management Functions
window.openProductModal = function() {
    openModal('addProductModal');
};

window.openBulkReviewModal = function() {
    openModal('bulkReviewModal');
    loadProductsForDropdown();
};

// Handle Add Product Submit
async function handleAddProductSubmit(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const productData = {
        name: formData.get('name'),
        description: formData.get('description'),
        price: parseFloat(formData.get('price')),
        category: formData.get('category'),
        imageUrl: formData.get('imageUrl') || null
    };
    
    try {
        const response = await fetch(`${API_BASE_URL}/products`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(productData)
        });
        
        const result = await response.json();
        
        if (!response.ok) {
            throw new Error(result.message || 'Failed to add product');
        }
        
        // Close modal
        closeModal('addProductModal');
        
        // Reset form
        event.target.reset();
        
        // Reload products
        await loadProducts();
        
        // Show success message
        showNotification('Ürün başarıyla eklendi!', 'success');
        
    } catch (error) {
        console.error('Error adding product:', error);
        showNotification('Ürün eklenirken hata oluştu. Lütfen tekrar deneyin.', 'error');
    }
}

// Load Products for Dropdown
async function loadProductsForDropdown() {
    try {
        const response = await fetch(`${API_BASE_URL}/products`);
        if (!response.ok) {
            throw new Error('Failed to load products');
        }
        
        const products = await response.json();
        const dropdown = document.getElementById('bulkProductId');
        
        dropdown.innerHTML = '<option value="">Ürün Seçin</option>';
        products.forEach(product => {
            const option = document.createElement('option');
            option.value = product.id;
            option.textContent = product.name;
            dropdown.appendChild(option);
        });
        
    } catch (error) {
        console.error('Error loading products for dropdown:', error);
    }
}

// Handle Bulk Review Submit
async function handleBulkReviewSubmit(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const productId = formData.get('productId');
    const reviewsText = formData.get('reviews');
    
    if (!productId || !reviewsText.trim()) {
        showNotification('Lütfen ürün seçin ve yorumları girin.', 'warning');
        return;
    }
    
    try {
        let reviews;
        try {
            reviews = JSON.parse(reviewsText);
        } catch (e) {
            showNotification('Yorumlar geçerli JSON formatında olmalıdır.', 'error');
            return;
        }
        
        if (!Array.isArray(reviews)) {
            showNotification('Yorumlar bir dizi olmalıdır.', 'error');
            return;
        }
        
        // Add reviews one by one
        let successCount = 0;
        for (const review of reviews) {
            try {
                const reviewData = {
                    productId: parseInt(productId),
                    starRating: review.starRating || 3,
                    comment: review.comment || 'Yorum'
                };
                
                const response = await fetch(`${API_BASE_URL}/reviews`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(reviewData)
                });
                
                if (response.ok) {
                    successCount++;
                }
            } catch (error) {
                console.error('Error adding review:', error);
            }
        }
        
        // Close modal
        closeModal('bulkReviewModal');
        
        // Reset form
        event.target.reset();
        
        // Reload products
        await loadProducts();
        
        // Show success message
        showNotification(`${successCount} yorum başarıyla eklendi!`, 'success');
        
    } catch (error) {
        console.error('Error submitting bulk reviews:', error);
        showNotification('Yorumlar eklenirken hata oluştu. Lütfen tekrar deneyin.', 'error');
    }
}



// Delete Product
window.deleteProduct = function(productId, productName) {
    if (confirm(`"${productName}" ürününü silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.`)) {
        deleteProductFromServer(productId);
    }
};

async function deleteProductFromServer(productId) {
    try {
        console.log('Deleting product with ID:', productId);
        
        const response = await fetch(`${API_BASE_URL}/products/${productId}`, {
            method: 'DELETE'
        });
        
        console.log('Delete response status:', response.status);
        
        if (!response.ok) {
            const errorText = await response.text();
            console.error('Delete failed:', errorText);
            throw new Error(`Failed to delete product: ${response.status} ${errorText}`);
        }
        
        // Reload products to show updated list
        await loadProducts();
        
        // Show success message
        showNotification('Ürün başarıyla silindi!', 'success');
        
    } catch (error) {
        console.error('Error deleting product:', error);
        showNotification('Ürün silinirken hata oluştu: ' + error.message, 'error');
    }
} 

// Delete Review
window.deleteReview = async function(reviewId, productId) {
    if (confirm('Bu yorumu silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.')) {
        try {
            const response = await fetch(`${API_BASE_URL}/reviews/${reviewId}`, {
                method: 'DELETE'
            });
            
            if (!response.ok) {
                throw new Error('Failed to delete review');
            }
            
            // Yorumlar modal'ını güncelle
            const reviewsModal = document.getElementById('reviewsModal');
            if (reviewsModal.style.display === 'block') {
                const reviewsModalContent = document.getElementById('reviewsModalContent');
                const productName = reviewsModalContent.querySelector('h2').textContent.split(' - ')[0];
                const reviews = await loadReviews(productId);
                displayReviewsModal(productId, productName, reviews);
            }
            
            // Ürün detay modal'ını güncelle
            const productModal = document.getElementById('productModal');
            if (productModal.style.display === 'block') {
                const product = await fetch(`${API_BASE_URL}/products/${productId}`).then(r => r.json());
                const reviews = await loadReviews(productId);
                displayProductDetail(product, reviews);
            }
            
            // Ürün listesini güncelle
            await loadProducts();
            
            // Başarı mesajı göster
            showNotification('Yorum başarıyla silindi!', 'success');
            
        } catch (error) {
            console.error('Error deleting review:', error);
            showNotification('Yorum silinirken hata oluştu. Lütfen tekrar deneyin.', 'error');
        }
    }
} 