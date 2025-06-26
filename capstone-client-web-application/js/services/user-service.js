let userService;

class UserService {
    currentUser = {};

    constructor() {
        this.loadUser();
    }

    saveUser(user) {
        this.currentUser = {
            token: user.token,
            userId: user.user.id,
            username: user.user.username,
            role: user.user.authorities[0].name
        };
        localStorage.setItem('user', JSON.stringify(this.currentUser));
        axios.defaults.headers.common['Authorization'] = `Bearer ${this.currentUser.token}`;
    }

    loadUser() {
        const user = localStorage.getItem('user');
        if (user) {
            this.currentUser = JSON.parse(user);
            axios.defaults.headers.common['Authorization'] = `Bearer ${this.currentUser.token}`;
        }
    }

    getHeaders() {
        return this.currentUser.token
            ? { 'Authorization': `Bearer ${this.currentUser.token}` }
            : {};
    }

    isLoggedIn() {
        return !!this.currentUser.token;
    }

    login(username, password) {
        const url = `${config.baseUrl}/login`;
        const data = { username, password };

        axios.post(url, data)
            .then(response => {
                this.saveUser(response.data);
                this.setHeaderLogin();
                productService.enableButtons();
                cartService.loadCart(() => {
                    cartService.updateCartDisplay();
                });
            })
            .catch(() => {
                templateBuilder.append("error", { error: "Login failed." }, "errors");
            });
    }

    logout() {
        localStorage.removeItem('user');
        delete axios.defaults.headers.common['Authorization'];
        this.currentUser = {};
        this.setHeaderLogin();
        productService.enableButtons();
        cartService.updateCartDisplay();
    }

    setHeaderLogin() {
        const data = {
            username: this.currentUser.username || '',
            loggedin: this.isLoggedIn(),
            loggedout: !this.isLoggedIn()
        };
        templateBuilder.build('header', data, 'header-user');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    userService = new UserService();
    userService.setHeaderLogin();
});
