let productService;

class ProductService {
    photos = [];

    filter = {
        cat: undefined,
        minPrice: undefined,
        maxPrice: undefined,
        color: undefined,
        queryString: function () {
            const qs = new URLSearchParams();
            if (this.cat !== undefined) qs.append("cat", this.cat);
            if (this.minPrice) qs.append("minPrice", this.minPrice);
            if (this.maxPrice) qs.append("maxPrice", this.maxPrice);
            if (this.color) qs.append("color", this.color);
            return qs.toString() ? `?${qs.toString()}` : "";
        }
    };

    constructor() {
        axios.get("/images/products/photos.json")
            .then(res => this.photos = res.data);
    }

    hasPhoto(photo) {
        return this.photos.includes(photo);
    }

    search() {
        const url = `${config.baseUrl}/products${this.filter.queryString()}`;

        axios.get(url)
            .then(res => {
                res.data.forEach(p => {
                    if (!this.hasPhoto(p.imageUrl)) {
                        p.imageUrl = "no-image.jpg";
                    }
                });

                templateBuilder.build("product", { products: res.data }, "content", this.enableButtons);
            })
            .catch(() => {
                templateBuilder.append("error", { error: "Searching products failed." }, "errors");
            });
    }

    enableButtons() {
        const buttons = [...document.querySelectorAll(".add-button")];
        buttons.forEach(b => {
            b.classList.toggle("invisible", !userService.isLoggedIn());
        });
    }

    // Filter setters
    addCategoryFilter(cat) {
        // Show All seçilirse (cat == 0), filtre temizlensin
        if (cat === 0 || cat === "0") {
            this.clearCategoryFilter();
        } else {
            this.filter.cat = cat;
        }
    }

    addMinPriceFilter(price) {
        this.filter.minPrice = price || undefined;
    }

    addMaxPriceFilter(price) {
        this.filter.maxPrice = price || undefined;
    }

    addColorFilter(color) {
        this.filter.color = color || undefined;
    }

    clearCategoryFilter() {
        this.filter.cat = undefined;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    productService = new ProductService();
});
