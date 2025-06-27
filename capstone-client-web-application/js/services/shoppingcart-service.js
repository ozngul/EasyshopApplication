let cartService;

class ShoppingCartService {
    cart = {
        items: [],
        total: 0
    };

    setCart(data) {
        this.cart.items = [];
        this.cart.total = data.total;
        for (const [_, value] of Object.entries(data.items)) {
            this.cart.items.push(value);
        }
    }

    addToCart(productId) {
        const url = `${config.baseUrl}/cart/products/${productId}`;
        axios.post(url, {}, { headers: userService.getHeaders() })
            .then(() => {
                this.loadCart(() => {
                    this.updateCartDisplay();
                });
            })
            .catch(() => {
                templateBuilder.append("error", { error: "Add to cart failed." }, "errors");
            });
    }

    loadCart(callback) {
        const url = `${config.baseUrl}/cart`;
        axios.get(url, { headers: userService.getHeaders() })
            .then(response => {
                this.setCart(response.data);
                this.updateCartDisplay();
                if (callback) callback();
            })
            .catch(() => {
                templateBuilder.append("error", { error: "Load cart failed." }, "errors");
            });
    }

    loadCartPage()
       {
           // templateBuilder.build("cart", this.cart, "main");

           const main = document.getElementById("main")
           main.innerHTML = "";

           let div = document.createElement("div");
           div.classList="filter-box";
           main.appendChild(div);

           const contentDiv = document.createElement("div")
           contentDiv.id = "content";
           contentDiv.classList.add("content-form");

           const cartHeader = document.createElement("div")
           cartHeader.classList.add("cart-header")

           const h1 = document.createElement("h1")
           h1.innerText = "Cart";
           cartHeader.appendChild(h1);

           const button = document.createElement("button");
           button.classList.add("btn")
           button.classList.add("btn-danger")
           button.innerText = "Clear";
           button.addEventListener("click", () => this.clearCart());
           cartHeader.appendChild(button)

           contentDiv.appendChild(cartHeader)
           main.appendChild(contentDiv);

           // let parent = document.getElementById("cart-item-list");
           this.cart.items.forEach(item => {
               this.buildItem(item, contentDiv)
           });
       }

  buildItem(item, parent) {
      const outerDiv = document.createElement("div");
      outerDiv.classList.add("cart-item");

      const titleDiv = document.createElement("div");
      const h4 = document.createElement("h4");
      h4.innerText = item.product.name;
      titleDiv.appendChild(h4);
      outerDiv.appendChild(titleDiv);

      const photoDiv = document.createElement("div");
      photoDiv.classList.add("photo");

      const img = document.createElement("img");
      img.src = `/images/products/${item.product.imageUrl}`;
      img.alt = item.product.name;
      img.addEventListener("click", () => {
          showImageDetailForm(item.product.name, img.src);
      });

      photoDiv.appendChild(img);

      const priceH4 = document.createElement("h4");
      priceH4.classList.add("price");
      priceH4.innerText = `$${item.product.price}`;
      photoDiv.appendChild(priceH4);

      outerDiv.appendChild(photoDiv);

      const descriptionDiv = document.createElement("div");
      descriptionDiv.classList.add("description");
      descriptionDiv.innerText = item.product.description;
      outerDiv.appendChild(descriptionDiv);

      const quantityDiv = document.createElement("div");
      quantityDiv.classList.add("quantity");
      quantityDiv.innerText = `Quantity: ${item.quantity}`;
      outerDiv.appendChild(quantityDiv);

      // ❌ Remove butonu kaldırıldı

      parent.appendChild(outerDiv);
  }


removeFromCart(productId) {
    if (!productId || isNaN(productId)) {
        console.error("Geçersiz productId:", productId);
        return;
    }

    const url = `${config.baseUrl}/cart/products/${productId}`;
    axios.delete(url, { headers: userService.getHeaders() })
        .then(() => {
            this.loadCart(() => {
                this.loadCartPage();
            });
        })
        .catch(() => {
            templateBuilder.append("error", { error: "Remove item failed." }, "errors");
        });
}


    clearCart() {
        const url = `${config.baseUrl}/cart`;
        axios.delete(url, { headers: userService.getHeaders() })
            .then(() => {
                this.cart = { items: [], total: 0 };
                this.updateCartDisplay();
                this.loadCartPage();
            })
            .catch(() => {
                templateBuilder.append("error", { error: "Empty cart failed." }, "errors");
            });
    }

   updateCartDisplay()
   {
       try {
           let totalQuantity = 0;
           this.cart.items.forEach(item => {
               totalQuantity += item.quantity;
           });

           const cartControl = document.getElementById("cart-items");
           if (cartControl)
               cartControl.innerText = totalQuantity;
       }
       catch (e) {
           console.error("Cart update failed:", e);
       }
   }
}

document.addEventListener('DOMContentLoaded', () => {
    cartService = new ShoppingCartService();
    if (userService.isLoggedIn()) {
        cartService.loadCart();
    }
});
