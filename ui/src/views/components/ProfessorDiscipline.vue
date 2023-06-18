<script>

import {DICTIONARY_API, PROFESSOR_DISCIPLINE_API} from "@/axios/axios";

export default {
	name: 'ProfessorDiscipline',
	data: () => ({
		professor: null,
		professors: [],
		discipline: [],
		disciplines: [],
		search: null,
		headers: [
			{
				text: 'ID',
				align: 'center',
				sortable: true,
				value: 'id'
			},
			{
				text: 'Преподаватель',
				align: 'center',
				sortable: true,
				value: 'professor.value'
			},
			{
				text: 'Дисциплина',
				align: 'center',
				sortable: true,
				value: 'discipline.value'
			},
			{
				text: 'Создано',
				align: 'center',
				sortable: true,
				value: 'createdDateTime'
			},
			{
				text: 'Изменено',
				align: 'center',
				sortable: true,
				value: 'updateDateTime'
			},
		],
		items: [],
	}),
	methods: {
		addLnk() {
			PROFESSOR_DISCIPLINE_API.post('addProfessorDiscipline', {
				professor: this.professor,
				disciplines: this.disciplines
			}).then(resp => {
				console.log(resp)
				this.fetchProfessorDisciplines(this.professor)
				this.disciplines = []
			}).catch(e => {
				console.log(e)
				alert(e.response.data.message)
			})
		},
		fetchProfessorDisciplines(professor) {
			PROFESSOR_DISCIPLINE_API.post('getProfessorDisciplines', professor).then(resp => {
				console.log(resp)
				this.items = resp.data
			}).catch(e => {
				console.log(e)
				alert(e.response.data.message)
			})
		}
	},
	mounted() {
		DICTIONARY_API.get('getAllByType', {
			params: {
				type: 'PROFESSOR'
			}
		}).then(resp => {
			console.log(resp)
			this.professors = resp.data
		}).catch(e => {
			console.log(e)
			alert(e.response.data.message)
		});
		DICTIONARY_API.get('getAllByType', {
			params: {
				type: 'DISCIPLINE'
			}
		}).then(resp => {
			console.log(resp)
			this.disciplines = resp.data
		}).catch(e => {
			console.log(e)
			alert(e.response.data.message)
		})
	},
	watch: {
		professor(item) {
			if (item) {
				this.fetchProfessorDisciplines(item)
			}
		}
	}
}
</script>

<template>
	<v-card>
		<v-card-title>
			Связь "Преподаватель - Дисциплина"
		</v-card-title>
		<v-card-text>
			<v-scheduleRowDataHolder>
				<v-col cols="4">
					<v-autocomplete
							v-model="professor"
							:items="professors"
							clearable
							dense
							hide-details
							item-text="value"
							item-value="id"
							label="Выберите Преподавателя"
							no-data-text="Нет данных"
							outlined
							return-object/>
				</v-col>
				<v-col cols="6">
					<v-select
							v-model="discipline"
							:items="disciplines"
							clearable
							dense
							hide-details
							item-text="value"
							item-value="id"
							label="Выберите Дисциплину"
							no-data-text="Нет данных"
							multiple
							outlined
							return-object/>
				</v-col>
				<v-col cols="2">
					<v-btn block color="#2edb5c" @click="addLnk">Добавить связь</v-btn>
				</v-col>
			</v-scheduleRowDataHolder>
			<v-scheduleRowDataHolder v-if="professor !== null && professor !== undefined">
				<v-col cols="12">
					<v-text-field v-model="search" append-icon="mdi-magnify" hide-details label="Поиск" single-line/>
				</v-col>
			</v-scheduleRowDataHolder>
			<v-scheduleRowDataHolder v-if="professor !== null && professor !== undefined">
				<v-col cols="12">
					<v-data-table
							:headers="headers"
							:items="items"
							:items-per-page="10"
							:search="search"
							class="elevation-1"
							item-key="id"
							no-data-text="Данные отсутствуют"/>
				</v-col>
			</v-scheduleRowDataHolder>
		</v-card-text>
	</v-card>
</template>

<style lang="scss">

</style>