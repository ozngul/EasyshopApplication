let profileService;

class ProfileService {
    loadProfile() {
        const url = `${config.baseUrl}/profile`;
        axios.get(url, { headers: userService.getHeaders() })
            .then(res => {
                templateBuilder.build("profile", res.data, "main");
            })
            .catch(() => {
                templateBuilder.append("error", { error: "Load profile failed." }, "errors");
            });
    }

    updateProfile(profile) {
        const url = `${config.baseUrl}/profile`;
        axios.put(url, profile, { headers: userService.getHeaders() })
            .then(() => {
                templateBuilder.append("message", { message: "Profile updated." }, "errors");
            })
            .catch(() => {
                templateBuilder.append("error", { error: "Save profile failed." }, "errors");
            });
    }
}

document.addEventListener("DOMContentLoaded", () => {
    profileService = new ProfileService();
});
