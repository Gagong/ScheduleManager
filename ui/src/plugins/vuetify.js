import Vue from 'vue';
import Vuetify from 'vuetify';
import ru from 'vuetify/lib/locale/ru';

Vue.use(Vuetify);

export default new Vuetify({
    lang: {
        current: 'ru',
        locales: { ru },
    },
});