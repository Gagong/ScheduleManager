<script>

import {DICTIONARY_API, PROFESSOR_DISCIPLINE_API, SCHEDULE_API} from "@/axios/axios";
import getRowColor, {FRIDAY, MONDAY, SATURDAY, THURSDAY, TIME, TIMES, TUESDAY, WEDNESDAY} from "@/constants/constants";

export default {
	name: 'CreateSchedule',
	data: () => ({
		monday: MONDAY,
		tuesday: TUESDAY,
		wednesday: WEDNESDAY,
		thursday: THURSDAY,
		friday: FRIDAY,
		saturday: SATURDAY,
		items: [],
		time: TIME,
		times: TIMES,
		dialog: false,
		selectedItem: {
			id: null,
			row: null,
			col: null,
			value: null,
			classroom: null,
			professor: null,
			discipline: null,
			disciplineType: null
		},
		selectedDiscipline: null,
		selectedClassroom: null,
		selectedProfessor: null,
		selectedDisciplineType: null,
		disciplines: [],
		classrooms: [],
		professors: [],
		disciplineTypes: [],
		rules: {
			required: value => !!value || 'Обязательное поле',
		},
	}),
	methods: {
		getRowColor,
		openCard(event, data) {
			this.selectedItem = data.item
			this.selectedClassroom = data.item.classroom
			this.selectedProfessor = data.item.professor
			this.selectedDiscipline = data.item.discipline
			this.selectedDisciplineType = data.item.disciplineType
			this.dialog = true
		},
		rollback() {
			this.selectedDiscipline = null
			this.selectedClassroom = null
			this.selectedProfessor = null
			this.selectedDisciplineType = null
			this.dialog = false
		},
		fillItem() {
			if (this.selectedClassroom === null || this.selectedProfessor === null || this.selectedDiscipline === null || this.selectedDisciplineType === null) {
				alert("Заполните все обязательные поля!")
				return
			}
			this.selectedItem.classroom = this.selectedClassroom
			this.selectedItem.professor = this.selectedProfessor
			this.selectedItem.discipline = this.selectedDiscipline
			this.selectedItem.disciplineType = this.selectedDisciplineType
			this.selectedItem.value = this.selectedClassroom.value + ', ' + this.selectedProfessor.value + ', ' + this.selectedDisciplineType.value + ', ' + this.selectedDiscipline.value
			for (let row in this.items) {
				for (let col in this.items[row]) {
					for (let item in this.items[row][col]) {
						let value = this.items[row][col][item]
						if (value.row === this.selectedItem.row && value.col === this.selectedItem.col && value.id === this.selectedItem.id) {
							this.items[row][col][item] = this.selectedItem
						}
					}
				}
			}
			this.selectedDiscipline = null
			this.selectedClassroom = null
			this.selectedProfessor = null
			this.selectedDisciplineType = null
			this.dialog = false
		},
		deleteItem() {
			for (let row in this.items) {
				for (let col in this.items[row]) {
					for (let item in this.items[row][col]) {
						for (let data in this.items[row][col][item]) {
							for (let v in this.items[row][col][item][data]) {
								let value = this.items[row][col][item][data][v]
								if (value.row === this.selectedItem.row && value.col === this.selectedItem.col && value.id === this.selectedItem.id) {
									SCHEDULE_API.delete(`delete/${value.id}`).then(() => {
										this.selectedItem.classroom = null
										this.selectedItem.professor = null
										this.selectedItem.discipline = null
										this.selectedItem.disciplineType = null
										this.selectedItem.value = "Нажмите для заполнения"
										this.items[row][col][item][data][v] = this.selectedItem
										this.selectedDiscipline = null
										this.selectedClassroom = null
										this.selectedProfessor = null
										this.selectedDisciplineType = null
										this.dialog = false
									}).catch(e => {
										console.log(e)
										alert(e.response.data.message)
									});
								}
							}
						}
					}
				}
			}
		},
		saveSchedule() {
			SCHEDULE_API.post('save', this.items).then(() => {
				SCHEDULE_API.get('getSchedule').then(resp => {
					this.items = resp.data
				}).catch(e => {
					console.log(e)
					alert(e.response.data.message)
				})
			}).catch(e => {
				console.log(e)
				alert(e.response.data.message)
			})
		}
	},
	mounted() {
		SCHEDULE_API.get('getSchedule').then(resp => {
			this.items = resp.data
		}).catch(e => {
			console.log(e)
			alert(e.response.data.message)
		});
		DICTIONARY_API.get('getAllByType', {
			params: {
				type: 'CLASSROOM'
			}
		}).then(resp => {
			this.classrooms = resp.data
		}).catch(e => {
			console.log(e)
			alert(e.response.data.message)
		});
		DICTIONARY_API.get('getAllByType', {
			params: {
				type: 'PROFESSOR'
			}
		}).then(resp => {
			this.professors = resp.data
		}).catch(e => {
			console.log(e)
			alert(e.response.data.message)
		});
		DICTIONARY_API.get('getAllByType', {
			params: {
				type: 'DISCIPLINE_TYPE'
			}
		}).then(resp => {
			this.disciplineTypes = resp.data
		}).catch(e => {
			console.log(e)
			alert(e.response.data.message)
		});
	},
	watch: {
		selectedProfessor(value) {
			if (value) {
				PROFESSOR_DISCIPLINE_API.post('getProfessorDisciplines', value).then(resp => {
					this.disciplines = resp.data.map(data => data.discipline)
				}).catch(e => {
					console.log(e)
					alert(e.response.data.message)
				})
			}
		}
	}
}
</script>

<template>
	<v-card>
		<v-dialog v-model="dialog" max-width="600px" persistent>
			<v-card>
				<v-card-title>Заполните информацию</v-card-title>
				<v-card-text v-if="items.length > 0">
					<v-row>
						<v-col cols="12">
							<v-autocomplete
									v-model="selectedClassroom"
									:items="classrooms"
									:rules="[rules.required]"
									clearable
									dense
									hide-details
									item-text="value"
									item-value="id"
									label="Выберите аудиторию"
									no-data-text="Нет данных"
									outlined
									return-object/>
						</v-col>
					</v-row>
					<v-row>
						<v-col cols="12">
							<v-autocomplete
									v-model="selectedProfessor"
									:items="professors"
									:rules="[rules.required]"
									clearable
									dense
									hide-details
									item-text="value"
									item-value="id"
									label="Выберите преподавателя"
									no-data-text="Нет данных"
									outlined
									return-object/>
						</v-col>
					</v-row>
					<v-row>
						<v-col cols="12">
							<v-autocomplete
									v-model="selectedDiscipline"
									:items="disciplines"
									:rules="[rules.required]"
									clearable
									dense
									hide-details
									item-text="value"
									item-value="id"
									label="Выберите дисциплину"
									no-data-text="Нет данных"
									outlined
									return-object/>
						</v-col>
					</v-row>
					<v-row>
						<v-col cols="12">
							<v-autocomplete
									v-model="selectedDisciplineType"
									:items="disciplineTypes"
									:rules="[rules.required]"
									clearable
									dense
									hide-details
									item-text="value"
									item-value="id"
									label="Выберите тип занятия"
									no-data-text="Нет данных"
									outlined
									return-object/>
						</v-col>
					</v-row>
					<v-row>
						<v-col cols="4">
							<v-btn block color="info" @click="rollback">Отмена</v-btn>
						</v-col>
						<v-col cols="4">
							<v-btn block color="success" @click="fillItem">Сохранить</v-btn>
						</v-col>
						<v-col cols="4">
							<v-btn block color="error" @click="deleteItem">Удалить</v-btn>
						</v-col>
					</v-row>
				</v-card-text>
			</v-card>
		</v-dialog>
		<v-card-title>Составление расписания</v-card-title>
		<v-card-text>
			<br/>
			<h3 style="text-align: center">Первая неделя</h3>
			<br/>
			<v-row>
				<v-col cols="1">
					<v-row>
						<v-col cols="12">
							<v-data-table
									:headers="time"
									:items="times"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="time"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
					</v-row>
				</v-col>
				<v-col cols="11">
					<v-row>
						<v-col cols="2">
							<v-data-table
									:headers="monday"
									:items="items[0].cols[0].items"
									:item-class="getRowColor"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:row="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="tuesday"
									:items="items[0].cols[1].items"
									:item-class="getRowColor"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:row="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="wednesday"
									:items="items[0].cols[2].items"
									:item-class="getRowColor"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:row="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="thursday"
									:items="items[0].cols[3].items"
									:item-class="getRowColor"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:row="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="friday"
									:items="items[0].cols[4].items"
									:item-class="getRowColor"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:row="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="saturday"
									:items="items[0].cols[5].items"
									:item-class="getRowColor"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:row="openCard"
							/>
						</v-col>
					</v-row>
				</v-col>
			</v-row>
			<br/>
			<h3 style="text-align: center">Вторая неделя</h3>
			<br/>
			<v-row>
				<v-col cols="1">
					<v-row>
						<v-col cols="12">
							<v-data-table
									:headers="time"
									:items="times"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="time"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
					</v-row>
				</v-col>
				<v-col cols="11">
					<v-row>
						<v-col cols="2">
							<v-data-table
									:headers="monday"
									:items="items[1].cols[0].items"
									:item-class="getRowColor"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:row="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="tuesday"
									:items="items[1].cols[1].items"
									:item-class="getRowColor"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:row="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="wednesday"
									:items="items[1].cols[2].items"
									:item-class="getRowColor"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:row="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="thursday"
									:items="items[1].cols[3].items"
									:item-class="getRowColor"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:row="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="friday"
									:items="items[1].cols[4].items"
									:item-class="getRowColor"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:row="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="saturday"
									:items="items[1].cols[5].items"
									:item-class="getRowColor"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:row="openCard"
							/>
						</v-col>
					</v-row>
				</v-col>
			</v-row>
			<v-row>
				<v-col cols="5"/>
				<v-col cols="2">
					<v-btn block color="success" @click="saveSchedule">Сохранить</v-btn>
				</v-col>
				<v-col cols="5"/>
			</v-row>
		</v-card-text>
	</v-card>
</template>

<style lang="css">
.custom-table td {
	font-size: 8pt !important;
	height: 85px !important;
}

.discipline_type_715 {
	background-color: #ffffff;
}

.discipline_type_716_717 {
	background-color: #2dd5ff;
}

.discipline_type_718 {
	background-color: #26ff3e;
}

.discipline_type_719 {
	background-color: #ff513c;
}

.discipline_type_720 {
	background-color: #ffdd2b;
}

.discipline_type_721 {
	background-color: #ac4bff;
}

.discipline_type_722 {
	background-color: #1affe1;
}
</style>