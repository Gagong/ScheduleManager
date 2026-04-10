<script>
import Navigator from "@/views/components/Navigator.vue";
import {EMPLOYEE_API} from "@/axios/axios";

export default {
  components: { Navigator },
  name: 'EmployeeAdmin',
  data: () => ({
    formData: {
      username: '',
      password: '',
      email: '',
      firstName: '',
      lastName: '',
      middleName: ''
    },
    errors: {},
    loading: false,
    success: false,
    showPassword: false,
    snackbar: {
      show: false,
      text: '',
      color: 'success',
      timeout: 5000
    },
    passwordRules: {
      minLength: false,
      hasUpperCase: false,
      hasLowerCase: false,
      hasNumber: false,
      hasSpecialChar: false
    },
    search: null,
    items: [],
    headers: [
      {
        text: 'Статус',
        align: 'center',
        sortable: true,
        value: 'status'
      },
      {
        text: 'ID',
        align: 'center',
        sortable: true,
        value: 'id'
      },
      {
        text: 'ФИО',
        align: 'left',
        sortable: true,
        value: 'name'
      },
      {
        text: 'E-Mail',
        align: 'center',
        sortable: true,
        value: 'email'
      },
      {
        text: 'Создано',
        align: 'center',
        sortable: true,
        value: 'createdDateTime'
      },
      {
        text: 'Действия',
        value: 'actions',
        align: 'center',
        width: '10%'
      },
    ],

    editDialog: {
      show: false,
      admin: null,
      loading: false
    }
  }),

  computed: {
    isPasswordValid() {
      return this.passwordRules.minLength &&
          this.passwordRules.hasUpperCase &&
          this.passwordRules.hasLowerCase &&
          this.passwordRules.hasNumber &&
          this.passwordRules.hasSpecialChar;
    },

    isFormValid() {
      return this.formData.username &&
          this.formData.username.length >= 3 &&
          this.isPasswordValid &&
          this.formData.email && /.+@.+\..+/.test(this.formData.email) &&
          this.formData.firstName &&
          this.formData.lastName &&
          Object.keys(this.errors).length === 0;
    }
  },

  watch: {
    'formData.password'(newVal) {
      this.validatePassword(newVal);
    }
  },

  methods: {
    validatePassword(password) {
      this.passwordRules = {
        minLength: password.length >= 8,
        hasUpperCase: /[A-Z]/.test(password),
        hasLowerCase: /[a-z]/.test(password),
        hasNumber: /[0-9]/.test(password),
        hasSpecialChar: /[!@#$%^&*(),.?":{}|<>]/.test(password)
      };
    },

    showSnackbar(text, color = 'success') {
      this.snackbar = {
        show: true,
        text: text,
        color: color,
        timeout: 5000
      };
    },

    async registerUser() {
      this.loading = true;
      this.errors = {};
      this.success = false;

      try {
        const response = await EMPLOYEE_API.post('/register', this.formData);
        this.success = true;
        this.resetForm();
        this.fetchEmployees()
        this.showSnackbar('Менеджер успешно зарегистрирован', 'success');
        console.log('Registration successful:', response.data);
      } catch (error) {
        if (error.response && error.response.status === 400) {
          if (error.response.data.errors) {
            error.response.data.errors.forEach(err => {
              this.errors[err.field] = err.message;
            });
            this.showSnackbar('Пожалуйста, исправьте ошибки в форме', 'warning');
          } else if (error.response.data.message) {
            this.showSnackbar(error.response.data.message, 'error');
          }
        } else {
          this.showSnackbar('Ошибка при регистрации - ' + error.response.data.message, 'error');
        }
        console.error('Registration error:', error);
      } finally {
        this.loading = false;
      }
    },

    resetForm() {
      this.formData = {
        username: '',
        password: '',
        email: '',
        firstName: '',
        lastName: '',
        middleName: ''
      };
      this.passwordRules = {
        minLength: false,
        hasUpperCase: false,
        hasLowerCase: false,
        hasNumber: false,
        hasSpecialChar: false
      };
    },

    clearError(field) {
      delete this.errors[field];
    },

    fetchEmployees() {
      this.loading = true;
      EMPLOYEE_API.get('/getEmployees').then(resp => {
        this.items = resp.data
        this.loading = false;
      })
    },

    getInitials(firstName, lastName) {
      if (!firstName && !lastName) return '?';
      const firstInitial = firstName ? firstName.charAt(0).toUpperCase() : '';
      const lastInitial = lastName ? lastName.charAt(0).toUpperCase() : '';
      return `${lastInitial}${firstInitial}`;
    },

    customFilter(value, search, item) {
      if (!search) return true;

      const searchLower = search.toLowerCase();
      const fullName = `${item.lastName} ${item.firstName} ${item.username} ${item.email}`.toLowerCase();

      return fullName.includes(searchLower);
    },

    async toggleAdminStatus(admin) {
      const action = admin.enabled ? 'заблокировать' : 'разблокировать';
      if (confirm(`Вы уверены, что хотите ${action} менеджера ${admin.lastName} ${admin.firstName}?`)) {
        try {
          const response = await EMPLOYEE_API.patch(`/employee/${admin.id}/toggle-status`);
          admin.enabled = response.data.enabled;
          this.showSnackbar(
              `Менеджер успешно ${admin.enabled ? 'разблокирован' : 'заблокирован'}`,
              'success'
          );
        } catch (error) {
          this.showSnackbar('Ошибка при изменении статуса', 'error');
          console.error('Error toggling admin status:', error);
        }
      }
    },

    editAdmin(admin) {
      this.editDialog.admin = { ...admin }; // Копируем объект
      this.editDialog.show = true;
    },

    async saveAdminChanges() {
      if (!this.editDialog.admin) return;

      this.editDialog.loading = true;

      try {
        await EMPLOYEE_API.put(`/employee/${this.editDialog.admin.id}`, {
          firstName: this.editDialog.admin.firstName,
          lastName: this.editDialog.admin.lastName,
          middleName: this.editDialog.admin.middleName,
          email: this.editDialog.admin.email,
          username: this.editDialog.admin.username
        });

        this.fetchEmployees()

        this.showSnackbar('Данные менеджера успешно обновлены', 'success');
        this.editDialog.show = false;
      } catch (error) {
        if (error.response && error.response.status === 400) {
          if (error.response.data.message) {
            this.showSnackbar(error.response.data.message, 'error');
          } else {
            this.showSnackbar('Ошибка валидации данных', 'error');
          }
        } else {
          this.showSnackbar('Ошибка при обновлении данных', 'error');
        }
        console.error('Error updating admin:', error);
      } finally {
        this.editDialog.loading = false;
      }
    },
  },

  mounted() {
    this.fetchEmployees()
  }
}
</script>

<template>
  <!-- eslint-disable -->
  <v-container fluid>
    <Navigator/>

    <!-- Snackbar для уведомлений -->
    <v-snackbar
        v-model="snackbar.show"
        :color="snackbar.color"
        :timeout="snackbar.timeout"
        :multi-line="true"
        :top="true"
    >
      {{ snackbar.text }}
      <template v-slot:actions>
        <v-btn
            color="white"
            variant="text"
            @click="snackbar.show = false"
        >
          Закрыть
        </v-btn>
      </template>
    </v-snackbar>

    <v-row justify="center" class="mt-8">
      <v-col cols="3">
        <v-card>
          <v-card-title class="text-h6 primary white--text pa-4">
            Регистрация нового менеджера
          </v-card-title>

          <v-card-text class="pa-6">
            <v-alert
                v-if="success"
                type="success"
                dismissible
                class="mb-4"
                @click:close="success = false"
            >
              Менеджер успешно зарегистрирован!
            </v-alert>

            <v-form @submit.prevent="registerUser">
              <v-row dense>
                <!-- Логин -->
                <v-col cols="12">
                  <v-text-field
                      dense
                      v-model="formData.username"
                      label="Логин *"
                      required
                      :error-messages="errors.username"
                      @input="clearError('username')"
                      counter="50"
                      :rules="[
                      v => !!v || 'Логин обязателен',
                      v => (v && v.length >= 3) || 'Минимум 3 символа',
                      v => (v && v.length <= 50) || 'Максимум 50 символов'
                    ]"
                  ></v-text-field>
                </v-col>
              </v-row>
              <v-row dense>
                <!-- Email -->
                <v-col cols="12">
                  <v-text-field
                      dense
                      v-model="formData.email"
                      label="Email *"
                      type="email"
                      required
                      :error-messages="errors.email"
                      @input="clearError('email')"
                      :rules="[
                      v => !!v || 'Email обязателен',
                      v => /.+@.+\..+/.test(v) || 'Некорректный email формат'
                    ]"
                  ></v-text-field>
                </v-col>
              </v-row>
              <v-row dense>
                <!-- Пароль -->
                <v-col cols="12">
                  <v-text-field
                      dense
                      v-model="formData.password"
                      :type="showPassword ? 'text' : 'password'"
                      label="Пароль *"
                      required
                      :error-messages="errors.password"
                      @input="clearError('password')"
                      :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                      @click:append="showPassword = !showPassword"
                  ></v-text-field>

                  <!-- Индикатор сложности пароля -->
                  <v-card v-if="formData.password" class="mt-2 pa-3" outlined>
                    <div class="password-requirements">
                      <div class="text-subtitle-2 mb-2">Требования к паролю:</div>
                      <div class="requirement-list">
                        <div class="requirement-item">
                          <v-icon :color="passwordRules.minLength ? 'success' : 'error'" size="16" class="mr-1">
                            {{ passwordRules.minLength ? 'mdi-check-circle' : 'mdi-close-circle' }}
                          </v-icon>
                          <span :class="passwordRules.minLength ? 'text-success' : 'text-error'" class="text-body-2">
                            Минимум 8 символов
                          </span>
                        </div>
                        <div class="requirement-item">
                          <v-icon :color="passwordRules.hasUpperCase ? 'success' : 'error'" size="16" class="mr-1">
                            {{ passwordRules.hasUpperCase ? 'mdi-check-circle' : 'mdi-close-circle' }}
                          </v-icon>
                          <span :class="passwordRules.hasUpperCase ? 'text-success' : 'text-error'" class="text-body-2">
                            Заглавная буква
                          </span>
                        </div>
                        <div class="requirement-item">
                          <v-icon :color="passwordRules.hasLowerCase ? 'success' : 'error'" size="16" class="mr-1">
                            {{ passwordRules.hasLowerCase ? 'mdi-check-circle' : 'mdi-close-circle' }}
                          </v-icon>
                          <span :class="passwordRules.hasLowerCase ? 'text-success' : 'text-error'" class="text-body-2">
                            Строчная буква
                          </span>
                        </div>
                        <div class="requirement-item">
                          <v-icon :color="passwordRules.hasNumber ? 'success' : 'error'" size="16" class="mr-1">
                            {{ passwordRules.hasNumber ? 'mdi-check-circle' : 'mdi-close-circle' }}
                          </v-icon>
                          <span :class="passwordRules.hasNumber ? 'text-success' : 'text-error'" class="text-body-2">
                            Цифра
                          </span>
                        </div>
                        <div class="requirement-item">
                          <v-icon :color="passwordRules.hasSpecialChar ? 'success' : 'error'" size="16" class="mr-1">
                            {{ passwordRules.hasSpecialChar ? 'mdi-check-circle' : 'mdi-close-circle' }}
                          </v-icon>
                          <span :class="passwordRules.hasSpecialChar ? 'text-success' : 'text-error'" class="text-body-2">
                            Специальный символ (!@#$%^&*)
                          </span>
                        </div>
                      </div>
                    </div>
                  </v-card>
                </v-col>
              </v-row>

              <v-row dense>
                <!-- Имя -->
                <v-col cols="12">
                  <v-text-field
                      dense
                      v-model="formData.firstName"
                      label="Имя *"
                      required
                      :error-messages="errors.firstName"
                      @input="clearError('firstName')"
                      :rules="[v => !!v || 'Имя обязательно']"
                  ></v-text-field>
                </v-col>
              </v-row>

              <v-row dense>
                <!-- Фамилия -->
                <v-col cols="12">
                  <v-text-field
                      dense
                      v-model="formData.lastName"
                      label="Фамилия *"
                      required
                      :error-messages="errors.lastName"
                      @input="clearError('lastName')"
                      :rules="[v => !!v || 'Фамилия обязательна']"
                  ></v-text-field>
                </v-col>
              </v-row>

              <v-row dense>
                <!-- Отчество -->
                <v-col cols="12">
                  <v-text-field
                      dense
                      v-model="formData.middleName"
                      label="Отчество"
                      :error-messages="errors.middleName"
                      @input="clearError('middleName')"
                  ></v-text-field>
                </v-col>
              </v-row>

              <v-alert
                  v-if="!isFormValid"
                  type="warning"
                  class="mt-4"
              >
                <div>Пожалуйста, заполните все обязательные поля правильно:</div>
                <ul class="mt-2">
                  <li v-if="!formData.username || formData.username.length < 3">Логин должен содержать минимум 3 символа</li>
                  <li v-if="!isPasswordValid">Пароль не соответствует требованиям безопасности</li>
                  <li v-if="!formData.email || !/.+@.+\..+/.test(formData.email)">Введите корректный email</li>
                  <li v-if="!formData.firstName">Укажите имя</li>
                  <li v-if="!formData.lastName">Укажите фамилию</li>
                </ul>
              </v-alert>

              <v-card-actions class="pa-0 mt-6">
                <v-btn
                    color="primary"
                    type="submit"
                    :loading="loading"
                    :disabled="!isFormValid || loading"
                    large
                    block
                >
                  Зарегистрировать
                </v-btn>
              </v-card-actions>

              <div class="text-caption text-center mt-4 text-grey">
                * - обязательные поля
              </div>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="9">
        <v-card>
          <v-card-title class="d-flex justify-space-between align-center">
            <div>
              <v-icon large color="primary" class="mr-2">mdi-account-group</v-icon>
              <span class="text-h6">Список менеджеров</span>
            </div>
            <v-chip color="primary" outlined>
              <v-icon left>mdi-account</v-icon>
              {{ items.length }} {{ items.length === 1 ? 'менеджер' : 'менеджеров' }}
            </v-chip>
          </v-card-title>

          <v-divider></v-divider>

          <v-card-text class="pa-4">
            <v-text-field
                v-model="search"
                append-icon="mdi-magnify"
                label="Поиск менеджеров"
                placeholder="Введите имя, фамилию или логин..."
                hide-details
                clearable
                outlined
                dense
                class="mb-4"
                prepend-inner-icon="mdi-account-search"
            ></v-text-field>

            <v-data-table
                :headers="headers"
                :items="items"
                :items-per-page="10"
                :search="search"
                :loading="loading"
                loading-text="Загрузка списка менеджеров..."
                no-data-text="Нет данных"
                no-results-text="По вашему запросу ничего не найдено"
                class="elevation-0"
                item-key="id"
                :custom-filter="customFilter"
            >
              <!-- Кастомный шаблон для статуса -->
              <template v-slot:item.status="{ item }">
                <v-chip
                    :color="item.enabled ? 'success' : 'error'"
                    dark
                    small
                >
                  {{ item.enabled ? 'Активен' : 'Заблокирован' }}
                </v-chip>
              </template>

              <!-- Кастомный шаблон для действий -->
              <template v-slot:item.actions="{ item }">
                <v-tooltip bottom>
                  <template v-slot:activator="{ on, attrs }">
                    <v-icon
                        v-bind="attrs"
                        v-on="on"
                        color="primary"
                        class="mr-2"
                        @click="editAdmin(item)"
                    >
                      mdi-pencil
                    </v-icon>
                  </template>
                  <span>Редактировать</span>
                </v-tooltip>

                <v-tooltip bottom>
                  <template v-slot:activator="{ on, attrs }">
                    <v-icon
                        v-bind="attrs"
                        v-on="on"
                        :color="item.enabled ? 'error' : 'success'"
                        @click="toggleAdminStatus(item)"
                    >
                      {{ item.enabled ? 'mdi-lock' : 'mdi-lock-open-variant' }}
                    </v-icon>
                  </template>
                  <span>{{ item.enabled ? 'Заблокировать' : 'Разблокировать' }}</span>
                </v-tooltip>
              </template>

              <template v-slot:item.name="{ item }">
                <div class="d-flex align-center">
                  <v-avatar size="32" class="mr-2" color="primary lighten-4">
                    <span class="text-primary font-weight-medium">
                      {{ getInitials(item.firstName, item.lastName) }}
                    </span>
                  </v-avatar>
                  <div>
                    <div class="font-weight-medium">{{ item.fullName }}</div>
                    <div class="caption text-grey">@{{ item.username }}</div>
                  </div>
                </div>
              </template>

              <template v-slot:item.email="{ item }">
                <div class="d-flex align-center">
                  <v-icon small class="mr-1">mdi-email</v-icon>
                  <span>{{ item.email }}</span>
                </div>
              </template>
            </v-data-table>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    <v-dialog v-model="editDialog.show" v-if="editDialog.admin" max-width="600px">
      <v-card>
        <v-card-title class="text-h5 primary white--text pa-4">
          Редактирование менеджера
        </v-card-title>

        <v-card-text class="pa-6">
          <v-form @submit.prevent="saveAdminChanges">
            <v-text-field
                v-model="editDialog.admin.username"
                label="Логин"
                disabled
                hint="Логин нельзя изменить"
                persistent-hint
            ></v-text-field>

            <v-text-field
                v-model="editDialog.admin.email"
                label="Email"
                :rules="[
                v => !!v || 'Email обязателен',
                v => /.+@.+\..+/.test(v) || 'Некорректный email формат'
              ]"
            ></v-text-field>

            <v-text-field
                v-model="editDialog.admin.firstName"
                label="Имя"
                :rules="[v => !!v || 'Имя обязательно']"
            ></v-text-field>

            <v-text-field
                v-model="editDialog.admin.lastName"
                label="Фамилия"
                :rules="[v => !!v || 'Фамилия обязательна']"
            ></v-text-field>

            <v-text-field
                v-model="editDialog.admin.middleName"
                label="Отчество"
            ></v-text-field>
          </v-form>
        </v-card-text>

        <v-card-actions class="pa-4">
          <v-spacer></v-spacer>
          <v-btn
              color="grey"
              @click="editDialog.show = false"
          >
            Отмена
          </v-btn>
          <v-btn
              color="primary"
              :loading="editDialog.loading"
              @click="saveAdminChanges"
          >
            Сохранить
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<style lang="scss" scoped>
.v-card {
  border-radius: 12px;
  overflow: hidden;
}

.v-card-title {
  background: linear-gradient(135deg, #1976D2 0%, #0D47A1 100%);
}

::v-deep .v-data-table {
  .v-data-table-header {
    background-color: #f5f5f5;
  }

  .v-chip {
    font-size: 12px;
  }
}

.requirement-list {
  display: flex;
  flex-direction: column;
  gap: 4px; /* Уменьшаем расстояние между элементами */
}

.requirement-item {
  display: flex;
  align-items: center;
  font-size: 13px;
  line-height: 1.2;
}

.text-success {
  color: #4caf50 !important;
}

.text-error {
  color: #f44336 !important;
}
</style>