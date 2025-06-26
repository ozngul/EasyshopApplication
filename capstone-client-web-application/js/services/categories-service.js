let categoryService;

class CategoryService {
    async getAllCategories() {
        const url = `${config.baseUrl}/categories`;
        try {
            const response = await axios.get(url);
            return response.data;
        } catch (error) {
            const data = { error: "Loading categories failed." };
            templateBuilder.append("error", data, "errors");
            throw error;
        }
    }
}

document.addEventListener('DOMContentLoaded', () => {
    categoryService = new CategoryService();
});
