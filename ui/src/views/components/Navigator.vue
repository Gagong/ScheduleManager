<template>
	<v-container fluid>
		<v-row>
			<v-toolbar dense flat>
        <v-toolbar-title class="d-flex align-center justify-start">
          <img :src="logo" height="35" class="mr-3" style="object-fit: contain" alt="none"/>
          <span class="d-flex align-center">Расписание ИГЭУ{{ username ? ' | ' + username : '' }}</span>
        </v-toolbar-title>
				<v-spacer/>
				<v-toolbar-items>
					<v-tabs v-model="tab">
						<v-tabs-slider color="#283593"/>
            <v-tab v-for="item in tabs" :key="item.label" exact :to="item.path">{{item.label}}</v-tab>
            <v-tab v-if="username" key="logout" @click="handleLogout">Выход</v-tab>
            <v-tab v-else key="login" to="/login">Авторизация</v-tab>
					</v-tabs>
				</v-toolbar-items>
			</v-toolbar>
		</v-row>
    <br>
	</v-container>
</template>

<style scoped>

</style>

<script>

import store from "@/store/store";
import router from "@/router/router";
import {LOGIN_API, PROFILE_API} from "@/axios/axios";
import logo from '@/assets/logo.png'

export default {
  //eslint-disable-next-line
	name: 'Navigator',
	data: () => ({
		tab: 0,
    tabs: [],
    logo: logo,
    username: null,
	}),
  setup() {

  },
  mounted() {
    this.upsertNavs()
    this.getUserData()
  },
  methods: {
    handleLogout() {
      store.dispatch('logout');
      this.username = null;
      this.upsertNavs()
      router.push('/');
    },
    upsertNavs() {
      PROFILE_API.get("getNavigator").then(resp => {
        this.tabs = resp.data
      })
    },
    getUserData() {
      LOGIN_API.get('me').then(resp => this.username = resp.data.fullName);
    }
  }
}
</script>
