<template>
	<div id="app">
		<v-app class="app-main-layout">
			<v-main>
        <router-view/>
      </v-main>
      <ErrorDisplay
          :auto-hide="true"
          :auto-hide-timeout="5000"
      />
		</v-app>
	</div>
</template>

<style lang="scss">
	#app {
		font-family: Avenir, Helvetica, Arial, sans-serif;
		-webkit-font-smoothing: antialiased;
		-moz-osx-font-smoothing: grayscale;
		color: #2c3e50;
	}
</style>

<script>

import ErrorDisplay from "@/views/components/ErrorDisplay.vue";

export default {
  name: 'App',

  components: {
    ErrorDisplay
  }
}
</script>