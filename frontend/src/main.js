
import { createApp } from "vue";
import { createPinia } from "pinia";

import App from "./App.vue";
import router from "./router";


import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";
import "bootstrap-icons/font/bootstrap-icons.css";
// them import icon
import { library } from '@fortawesome/fontawesome-svg-core';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons'; // Import 2 icon cần dùng

library.add(faEye, faEyeSlash); // Thêm icon vào thư viện

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);
app.use(router);
app.component('font-awesome-icon', FontAwesomeIcon); // Đăng ký component toàn cục

app.mount("#app");
