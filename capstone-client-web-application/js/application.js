function showLoginForm() {
    templateBuilder.build('login-form', {}, 'login');
}

function hideModalForm() {
    templateBuilder.clear('login');
}

function login() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    userService.login(username, password);
    hideModalForm();
}

function showImageDetailForm(product, imageUrl) {
    const imageDetail = {
        name: product,
        imageUrl: imageUrl
    };

    templateBuilder.build('image-detail', imageDetail, 'login');
}

async function loadHome() {
    templateBuilder.build('home', {}, 'main');

    try {
        const categories = await categoryService.getAllCategories();
        loadCategories(categories);
    } catch (error) {
        // Hata zaten categoryService içinde gösteriliyor
    }

    productService.search();
}

function editProfile() {
    profileService.loadProfile();
}

function saveProfile() {
    const profile = {
        firstName: document.getElementById("firstName").value,
        lastName: document.getElementById("lastName").value,
        phone: document.getElementById("phone").value,
        email: document.getElementById("email").value,
        address: document.getElementById("address").value,
        city: document.getElementById("city").value,
        state: document.getElementById("state").value,
        zip: document.getElementById("zip").value
    };

    profileService.updateProfile(profile);
}

function showCart() {
    cartService.loadCart(() => {
        cartService.loadCartPage();
    });
}

function clearCart() {
    cartService.clearCart(() => {
        cartService.loadCartPage();
    });
}

function setCategory(control) {
    productService.addCategoryFilter(control.value);
    productService.search();
}

function setColor(control) {
    productService.addColorFilter(control.value);
    productService.search();
}

function setMinPrice(control) {
    const label = document.getElementById("min-price-display");
    label.innerText = control.value;

    const value = control.value != 0 ? control.value : "";
    productService.addMinPriceFilter(value);
    productService.search();
}

function setMaxPrice(control) {
    const label = document.getElementById("max-price-display");
    label.innerText = control.value;

    const value = control.value != 1500 ? control.value : "";
    productService.addMaxPriceFilter(value);
    productService.search();
}

function closeError(control) {
    setTimeout(() => {
        control.click();
    }, 3000);
}

document.addEventListener('DOMContentLoaded', () => {
    userService = new UserService();
    userService.setHeaderLogin();

    cartService = new ShoppingCartService();

    productService = new ProductService();
    categoryService = new CategoryService();
    profileService = new ProfileService();

    // 🔐 Yalnızca kullanıcı giriş yaptıysa sepeti yükle
    if (userService.isLoggedIn()) {
        setTimeout(() => {
            cartService.loadCart();
        }, 100);
    }

    loadHome();
});
