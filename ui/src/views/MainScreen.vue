<template>
	<v-container fluid>
		<v-row>
			<v-toolbar dense flat>
				<v-toolbar-title>Менеджер расписания | {{username}}</v-toolbar-title>
				<v-spacer/>
				<v-toolbar-items>
					<v-tabs v-model="tab">
						<v-tabs-slider color="#283593"/>
						<v-tab>Справочники</v-tab>
						<v-tab>Преподаватель - Дисциплина</v-tab>
						<v-tab>Расписание</v-tab>
						<v-tab>Составление расписания</v-tab>
            <v-tab @click="handleLogout">Выход</v-tab>
					</v-tabs>
				</v-toolbar-items>
			</v-toolbar>
		</v-row>
		<v-row>
			<v-col cols="12">
				<v-tabs-items v-model="tab">
					<!--Справочники-->
					<v-tab-item>
						<v-row>
							<v-col cols="1"/>
							<v-col cols="2">
								<v-toolbar flat>
									<v-toolbar-items>
										<v-tabs style="height: 1000px" v-model="dictionaryTab" vertical>
											<v-tabs-slider color="#283593"/>
											<v-tab v-for="dictionary in dictionaries" :key="dictionary.key" class="left-tab">
                        {{dictionary.value }}
											</v-tab>
										</v-tabs>
									</v-toolbar-items>
								</v-toolbar>
							</v-col>
							<v-col cols="8">
								<v-tabs-items v-model="dictionaryTab">
									<v-tab-item v-for="dictionary in dictionaries" :key="dictionary.key">
										<DictionaryTab :dictionary="dictionary"/>
									</v-tab-item>
								</v-tabs-items>
							</v-col>
							<v-col cols="1"/>
						</v-row>
					</v-tab-item>
					<!--Преподаватель-Дисциплина-->
					<v-tab-item>
						<v-row>
							<v-col cols="1"/>
							<v-col cols="10">
								<ProfessorDiscipline/>
							</v-col>
							<v-col cols="1"/>
						</v-row>
					</v-tab-item>
					<!--Расписание-->
					<v-tab-item>
						<GeneralSchedule/>
					</v-tab-item>
					<!--Составление расписания-->
					<v-tab-item>
						<CreateSchedule/>
					</v-tab-item>
				</v-tabs-items>
			</v-col>
		</v-row>
	</v-container>
</template>

<style scoped>
.left-tab {
	justify-content: left !important;
}
</style>

<script>
import {DICTIONARY_API} from "@/axios/axios";
import DictionaryTab from "@/views/components/DictionaryTab.vue";
import ProfessorDiscipline from "@/views/components/ProfessorDiscipline.vue";
import CreateSchedule from "@/views/components/CreateSchedule.vue";
import GeneralSchedule from "@/views/components/GeneralSchedule.vue";
import store from "@/store";
import router from "@/router";
import {computed} from "vue";

export default {
	name: 'MainScreen',
	components: {
		GeneralSchedule,
		CreateSchedule,
		ProfessorDiscipline,
		DictionaryTab
	},
	data: () => ({
		tab: 0,
		dictionaries: [],
		dictionaryTab: 0,
	}),
  setup() {
    const username = computed(() => store.getters.username);
    return {
      username
    }
  },
	mounted() {
		DICTIONARY_API.get('getAll')
			.then(resp => {
				this.dictionaries = resp.data
        console.log(resp.data)
        console.log(this.dictionaries)
			})
			.catch(e => {
				console.log(e)
			})
	},
  methods: {
    handleLogout() {
      store.dispatch('logout');
      router.push('/login');
    }
  }
}
</script>
