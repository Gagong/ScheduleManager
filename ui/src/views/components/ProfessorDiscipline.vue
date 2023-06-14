<script>

import {DICTIONARY_API} from "@/axios/axios";

export default {
	name: 'ProfessorDiscipline',
	data: () => ({
		professor: null,
		professors: [],
		discipline: [],
		disciplines: [],
	}),
	methods: {},
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
}
</script>

<template>
	<v-card height="1000">
		<v-card-title>
			Связь "Преподаватель - Дисциплина"
		</v-card-title>
		<v-card-text>
			<v-row>
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
							multiple
							outlined
							return-object/>
				</v-col>
				<v-col cols="2">
					<v-btn block color="#2edb5c">Добавить связь</v-btn>
				</v-col>
			</v-row>
			<v-row>

			</v-row>
		</v-card-text>
	</v-card>
</template>

<style lang="scss" scoped>

</style>