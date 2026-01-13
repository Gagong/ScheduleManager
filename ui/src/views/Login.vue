<template>
  <v-container fluid class="fill-height">
    <v-row justify="center" align="center">
      <v-col cols="12" sm="8" md="6" lg="4">
        <v-card class="pa-6">
          <v-card-title class="text-center justify-center">
            <h2 class="text-h4 font-weight-bold">
              Менеджер расписания
            </h2>
            <h3 class="text-h6 mt-2">
              Авторизация
            </h3>
          </v-card-title>

          <v-card-text>
            <v-form @submit.prevent="handleLogin">
              <v-text-field
                  v-model="username"
                  label="Логин"
                  placeholder="Введите свой логин"
                  prepend-inner-icon="mdi-account"
                  variant="outlined"
                  required
                  :disabled="loading"
                  class="mb-4"
              />

              <v-text-field
                  v-model="password"
                  label="Пароль"
                  placeholder="Введите свой пароль"
                  prepend-inner-icon="mdi-lock"
                  variant="outlined"
                  type="password"
                  required
                  :disabled="loading"
                  class="mb-4"
              />

              <v-btn
                  type="submit"
                  color="primary"
                  size="large"
                  block
                  :loading="loading"
                  :disabled="loading"
              >
                {{ loading ? 'Авторизация...' : 'Войти' }}
              </v-btn>

              <v-alert
                  v-if="error"
                  type="error"
                  variant="tonal"
                  class="mt-4"
              >
                {{ error }}
              </v-alert>
            </v-form>
          </v-card-text>

          <v-card-text>
            <v-alert type="info" variant="tonal">
              <template v-slot:title>
                Данные по умолчанию
              </template>
              <div class="mt-2">
                <div><strong>Логин:</strong> voronenkov</div>
                <div><strong>Пароль:</strong> admin</div>
              </div>
            </v-alert>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import { ref } from 'vue';
import router from "@/router";
import store from "@/store";

export default {
  name: 'LoginView',
  setup() {
    const username = ref('');
    const password = ref('');
    const error = ref('');
    const loading = ref(false);

    const handleLogin = async () => {
      error.value = '';
      loading.value = true;

      try {
        const result = await store.dispatch('login', {
          username: username.value,
          password: password.value
        });

        if (result.success) {
          router.push('/');
        } else {
          error.value = result.error;
        }
      } catch (err) {
        error.value = 'Авторизация неудачна. Повторите попытку.';
      } finally {
        loading.value = false;
      }
    };

    return {
      username,
      password,
      error,
      loading,
      handleLogin,
    };
  },
};
</script>

<style scoped>
.fill-height {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
</style>