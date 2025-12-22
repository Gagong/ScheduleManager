<template>
  <div class="login-container">
    <div class="login-card">
      <h2>
        Менеджер расписания
        <br>
        Авторизация
      </h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">Логин</label>
          <input
              id="username"
              v-model="username"
              type="text"
              required
              placeholder="Введите свой логин"
          />
        </div>

        <div class="form-group">
          <label for="password">Пароль</label>
          <input
              id="password"
              v-model="password"
              type="password"
              required
              placeholder="Введите свой пароль"
          />
        </div>

        <button type="submit" :disabled="loading">
          {{ loading ? 'Авторизация...' : 'Войти' }}
        </button>

        <div v-if="error" class="error-message">
          {{ error }}
        </div>
      </form>

      <div class="login-info">
        <p>Данные по умолчанию:</p>
        <p><strong>Логин:</strong> voronenkov</p>
        <p><strong>Пароль:</strong> admin</p>
      </div>
    </div>
  </div>
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
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h2 {
  text-align: center;
  margin-bottom: 1.5rem;
  color: #333;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: #555;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1rem;
}

input:focus {
  outline: none;
  border-color: #667eea;
}

button {
  width: 100%;
  padding: 0.75rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  margin-top: 1rem;
}

button:hover {
  background: #5a67d8;
}

button:disabled {
  background: #a0aec0;
  cursor: not-allowed;
}

.error-message {
  margin-top: 1rem;
  padding: 0.75rem;
  background: #fed7d7;
  color: #c53030;
  border-radius: 5px;
  font-size: 0.875rem;
}

.login-info {
  margin-top: 1.5rem;
  padding: 1rem;
  background: #f7fafc;
  border-radius: 5px;
  font-size: 0.875rem;
  color: #4a5568;
}

.login-info p {
  margin: 0.25rem 0;
}
</style>