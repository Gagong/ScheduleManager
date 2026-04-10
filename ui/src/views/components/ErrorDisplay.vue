<template>
  <v-snackbar
      v-model="showError"
      :color="snackbarColor"
      :timeout="snackbarTimeout"
      :top="true"
      :right="true"
      :multi-line="true"
      :vertical="expanded"
  >
    <div class="error-content">
      <div class="d-flex align-center">
        <v-icon class="mr-3" :color="iconColor" v-text="errorIcon" />

        <div class="flex-grow-1">
          <div class="d-flex align-center">
            <span class="error-status mr-2" v-if="error?.status">
              <v-chip small :color="statusChipColor" dark>
                {{ error.status }}
              </v-chip>
            </span>
            <span class="error-message font-weight-medium">
              {{ error?.message || 'Произошла ошибка' }}
            </span>
          </div>

          <v-expand-transition>
            <div v-if="expanded" class="error-details mt-2">
              <v-divider class="my-2" :dark="isDark" />

              <v-list dense :dark="isDark" class="transparent">
                <v-list-item v-if="error?.url">
                  <v-list-item-icon>
                    <v-icon small>mdi-link</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title class="text-caption">
                      {{ error?.data?.path }}
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>

                <v-list-item v-if="error?.method">
                  <v-list-item-icon>
                    <v-icon small>mdi-api</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title class="text-caption">
                      Метод: {{ error.method }}
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>

                <v-list-item v-if="error?.timestamp">
                  <v-list-item-icon>
                    <v-icon small>mdi-clock-outline</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title class="text-caption">
                      {{ formatTime(error.timestamp) }}
                    </v-list-item-title>
                  </v-list-item-content>
                </v-list-item>

                <v-list-item v-if="error?.data?.trace">
                  <v-list-item-icon>
                    <v-icon small>mdi-clock-outline</v-icon>
                  </v-list-item-icon>
                  <v-list-item-content>
                    <v-list-item-title class="text-caption">
                      Подробное описание ошибки
                      <v-btn
                          v-if="!expandedTrace"
                          icon
                          small
                          @click="toggleExpandTrace"
                          class="ml-2"
                      >
                        <v-icon>mdi-chevron-down</v-icon>
                      </v-btn>

                      <v-btn
                          v-if="expandedTrace"
                          icon
                          small
                          @click="toggleExpandTrace"
                          class="ml-2"
                      >
                        <v-icon>mdi-chevron-up</v-icon>
                      </v-btn>
                    </v-list-item-title>
                    <v-expand-transition>
                      <div v-if="expandedTrace" class="error-details mt-2">
                        <v-divider class="my-2" :dark="isDark" />
                        <v-textarea :value="error?.data?.trace"/>
                      </div>
                    </v-expand-transition>
                  </v-list-item-content>
                </v-list-item>
              </v-list>
            </div>
          </v-expand-transition>
        </div>

        <v-btn
            v-if="!expanded && hasDetails"
            icon
            small
            @click="toggleExpand"
            class="ml-2"
        >
          <v-icon>mdi-chevron-down</v-icon>
        </v-btn>

        <v-btn
            v-if="expanded"
            icon
            small
            @click="toggleExpand"
            class="ml-2"
        >
          <v-icon>mdi-chevron-up</v-icon>
        </v-btn>
      </div>
    </div>

    <template v-slot:action="{ attrs }">
      <v-btn
          v-bind="attrs"
          text
          @click="dismissError"
      >
        Закрыть
      </v-btn>
    </template>
  </v-snackbar>
</template>

<script>

import {emitter} from "@/axios/axios";

export default {
  name: 'ErrorDisplay',

  props: {
    autoHide: {
      type: Boolean,
      default: true
    },
    autoHideTimeout: {
      type: Number,
      default: 5000
    }
  },

  data() {
    return {
      error: null,
      expanded: false,
      expandedTrace: false
    }
  },

  computed: {
    showError: {
      get() {
        return !!this.error && !window.location.href.includes("login") && !window.location.href.includes("employee")
      },
      set(value) {
        if (!value) this.dismissError()
      }
    },

    snackbarColor() {
      const status = this.error?.status
      if (status >= 500) return 'error'
      if (status === 404) return 'warning'
      if (status === 401 || status === 403) return 'orange'
      if (status >= 400) return 'warning'
      return 'info'
    },

    iconColor() {
      return 'white'
    },

    errorIcon() {
      const status = this.error?.status
      if (status >= 500) return 'mdi-server-network'
      if (status === 404) return 'mdi-map-search'
      if (status === 401 || status === 403) return 'mdi-lock'
      if (status >= 400) return 'mdi-alert-circle'
      return 'mdi-alert'
    },

    statusChipColor() {
      const status = this.error?.status
      if (status >= 500) return 'red darken-3'
      if (status === 404) return 'orange darken-3'
      if (status === 401 || status === 403) return 'orange darken-3'
      if (status >= 400) return 'orange darken-3'
      return 'grey darken-3'
    },

    snackbarTimeout() {
      if (!this.autoHide) return -1
      if (this.error?.status < 500) return this.autoHideTimeout
      return -1
    },

    isDark() {
      return this.$vuetify.theme.dark
    },

    hasDetails() {
      return this.error?.url || this.error?.method || this.error?.timestamp
    }
  },

  mounted() {
    emitter.on('api-error', this.handleApiError)
  },

  beforeDestroy() {
    emitter.off('api-error', this.handleApiError)
  },

  methods: {
    handleApiError(errorInfo) {
      this.error = errorInfo
      this.expanded = false
      this.expandedTrace = false
      console.error('API Error:', errorInfo)
    },

    dismissError() {
      this.error = null
      this.expanded = false
      this.expandedTrace = false
    },

    toggleExpand() {
      this.expanded = !this.expanded
    },

    toggleExpandTrace() {
      this.expandedTrace = !this.expandedTrace
    },

    formatTime(timestamp) {
      if (!timestamp) return ''
      const date = new Date(timestamp)
      return date.toLocaleString('ru-RU', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        day: '2-digit',
        month: '2-digit'
      })
    }
  },
}
</script>

<style scoped>
.error-content {
  width: 100%;
  min-width: 300px;
  max-width: 500px;
}

.error-message {
  word-break: break-word;
  line-height: 1.4;
}

.error-details {
  font-size: 0.875rem;
  opacity: 0.9;
}

/* Анимация для иконки */
.v-icon {
  transition: transform 0.2s;
}

.error-content:hover .v-icon {
  transform: scale(1.1);
}

/* Адаптивность */
@media (max-width: 600px) {
  .error-content {
    min-width: 250px;
    max-width: 300px;
  }
}
</style>