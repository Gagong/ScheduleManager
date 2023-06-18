<script>

import {DICTIONARY_API, PROFESSOR_DISCIPLINE_API, SCHEDULE_API} from "@/axios/axios";

export default {
	name: 'CreateSchedule',
	data: () => ({
		monday: [
			{
				text: 'Понедельник',
				align: 'center',
				sortable: false,
				value: 'value'
			},
		],
		tuesday: [
			{
				text: 'Вторник',
				align: 'center',
				sortable: false,
				value: 'value'
			},
		],
		wednesday: [
			{
				text: 'Среда',
				align: 'center',
				sortable: false,
				value: 'value'
			},
		],
		thursday: [
			{
				text: 'Четверг',
				align: 'center',
				sortable: false,
				value: 'value'
			},
		],
		friday: [
			{
				text: 'Пятница',
				align: 'center',
				sortable: false,
				value: 'value'
			},
		],
		saturday: [
			{
				text: 'Суббота',
				align: 'center',
				sortable: false,
				value: 'value'
			},
		],
		items: [
			/*[
				[
					{
						id: 1,
						scheduleRowDataHolder: 1,
						col: 1,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 2,
						scheduleRowDataHolder: 1,
						col: 1,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 3,
						scheduleRowDataHolder: 1,
						col: 1,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 4,
						scheduleRowDataHolder: 1,
						col: 1,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 5,
						scheduleRowDataHolder: 1,
						col: 1,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 6,
						scheduleRowDataHolder: 1,
						col: 1,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 7,
						scheduleRowDataHolder: 1,
						col: 1,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					}
				],
				[
					{
						id: 1,
						scheduleRowDataHolder: 1,
						col: 2,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 2,
						scheduleRowDataHolder: 1,
						col: 2,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 3,
						scheduleRowDataHolder: 1,
						col: 2,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 4,
						scheduleRowDataHolder: 1,
						col: 2,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 5,
						scheduleRowDataHolder: 1,
						col: 2,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 6,
						scheduleRowDataHolder: 1,
						col: 2,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 7,
						scheduleRowDataHolder: 1,
						col: 2,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					}
				],
				[
					{
						id: 1,
						scheduleRowDataHolder: 1,
						col: 3,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 2,
						scheduleRowDataHolder: 1,
						col: 3,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 3,
						scheduleRowDataHolder: 1,
						col: 3,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 4,
						scheduleRowDataHolder: 1,
						col: 3,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 5,
						scheduleRowDataHolder: 1,
						col: 3,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 6,
						scheduleRowDataHolder: 1,
						col: 3,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 7,
						scheduleRowDataHolder: 1,
						col: 3,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					}
				],
				[
					{
						id: 1,
						scheduleRowDataHolder: 1,
						col: 4,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 2,
						scheduleRowDataHolder: 1,
						col: 4,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 3,
						scheduleRowDataHolder: 1,
						col: 4,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 4,
						scheduleRowDataHolder: 1,
						col: 4,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 5,
						scheduleRowDataHolder: 1,
						col: 4,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 6,
						scheduleRowDataHolder: 1,
						col: 4,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 7,
						scheduleRowDataHolder: 1,
						col: 4,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					}
				],
				[
					{
						id: 1,
						scheduleRowDataHolder: 1,
						col: 5,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 2,
						scheduleRowDataHolder: 1,
						col: 5,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 3,
						scheduleRowDataHolder: 1,
						col: 5,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 4,
						scheduleRowDataHolder: 1,
						col: 5,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 5,
						scheduleRowDataHolder: 1,
						col: 5,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 6,
						scheduleRowDataHolder: 1,
						col: 5,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 7,
						scheduleRowDataHolder: 1,
						col: 5,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					}
				],
				[
					{
						id: 1,
						scheduleRowDataHolder: 1,
						col: 6,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 2,
						scheduleRowDataHolder: 1,
						col: 6,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 3,
						scheduleRowDataHolder: 1,
						col: 6,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 4,
						scheduleRowDataHolder: 1,
						col: 6,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 5,
						scheduleRowDataHolder: 1,
						col: 6,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 6,
						scheduleRowDataHolder: 1,
						col: 6,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 7,
						scheduleRowDataHolder: 1,
						col: 6,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					}
				],
			],
			[
				[
					{
						id: 1,
						scheduleRowDataHolder: 2,
						col: 1,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 2,
						scheduleRowDataHolder: 2,
						col: 1,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 3,
						scheduleRowDataHolder: 2,
						col: 1,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 4,
						scheduleRowDataHolder: 2,
						col: 1,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 5,
						scheduleRowDataHolder: 2,
						col: 1,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 6,
						scheduleRowDataHolder: 2,
						col: 1,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 7,
						scheduleRowDataHolder: 2,
						col: 1,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					}
				],
				[
					{
						id: 1,
						scheduleRowDataHolder: 2,
						col: 2,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 2,
						scheduleRowDataHolder: 2,
						col: 2,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 3,
						scheduleRowDataHolder: 2,
						col: 2,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 4,
						scheduleRowDataHolder: 2,
						col: 2,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 5,
						scheduleRowDataHolder: 2,
						col: 2,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 6,
						scheduleRowDataHolder: 2,
						col: 2,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 7,
						scheduleRowDataHolder: 2,
						col: 2,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					}
				],
				[
					{
						id: 1,
						scheduleRowDataHolder: 2,
						col: 3,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 2,
						scheduleRowDataHolder: 2,
						col: 3,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 3,
						scheduleRowDataHolder: 2,
						col: 3,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 4,
						scheduleRowDataHolder: 2,
						col: 3,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 5,
						scheduleRowDataHolder: 2,
						col: 3,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 6,
						scheduleRowDataHolder: 2,
						col: 3,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 7,
						scheduleRowDataHolder: 2,
						col: 3,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					}
				],
				[
					{
						id: 1,
						scheduleRowDataHolder: 2,
						col: 4,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 2,
						scheduleRowDataHolder: 2,
						col: 4,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 3,
						scheduleRowDataHolder: 2,
						col: 4,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 4,
						scheduleRowDataHolder: 2,
						col: 4,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 5,
						scheduleRowDataHolder: 2,
						col: 4,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 6,
						scheduleRowDataHolder: 2,
						col: 4,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 7,
						scheduleRowDataHolder: 2,
						col: 4,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					}
				],
				[
					{
						id: 1,
						scheduleRowDataHolder: 2,
						col: 5,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 2,
						scheduleRowDataHolder: 2,
						col: 5,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 3,
						scheduleRowDataHolder: 2,
						col: 5,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 4,
						scheduleRowDataHolder: 2,
						col: 5,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 5,
						scheduleRowDataHolder: 2,
						col: 5,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 6,
						scheduleRowDataHolder: 2,
						col: 5,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 7,
						scheduleRowDataHolder: 2,
						col: 5,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					}
				],
				[
					{
						id: 1,
						scheduleRowDataHolder: 2,
						col: 6,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 2,
						scheduleRowDataHolder: 2,
						col: 6,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 3,
						scheduleRowDataHolder: 2,
						col: 6,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 4,
						scheduleRowDataHolder: 2,
						col: 6,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 5,
						scheduleRowDataHolder: 2,
						col: 6,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 6,
						scheduleRowDataHolder: 2,
						col: 6,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					},
					{
						id: 7,
						scheduleRowDataHolder: 2,
						col: 6,
						value: "Нажмите для заполнения",
						classroom: null,
						professor: null,
						discipline: null
					}
				],
			]*/
		],
		time: [
			{
				text: 'Время',
				align: 'center',
				sortable: false,
				value: 'time'
			},
		],
		times: [
			{
				time: "8:00 - 9:35"
			},
			{
				time: "9:50 - 11:25"
			},
			{
				time: "11:40 - 13:15"
			},
			{
				time: "14:00 - 15:35"
			},
			{
				time: "15:50 - 17:25"
			},
			{
				time: "17:40 - 19:15"
			},
			{
				time: "19:25 - 21:00"
			}
		],
		dialog: false,
		selectedItem: {
			id: null,
			scheduleRowDataHolder: null,
			col: null,
			value: null,
			classroom: null,
			professor: null,
			discipline: null
		},
		selectedDiscipline: null,
		selectedClassroom: null,
		selectedProfessor: null,
		disciplines: [],
		classrooms: [],
		professors: [],
		rules: {
			required: value => !!value || 'Обязательное поле',
		},
	}),
	methods: {
		openCard(event, data) {
			this.selectedItem = data.item
			this.dialog = true
		},
		rollback() {
			this.selectedDiscipline = null
			this.selectedClassroom = null
			this.selectedProfessor = null
			this.dialog = false
		},
		fillItem() {
			if (this.selectedClassroom === null || this.selectedProfessor === null || this.selectedDiscipline === null) {
				alert("Заполните все обязательные поля!")
				return
			}
			this.selectedItem.classroom = this.selectedClassroom
			this.selectedItem.professor = this.selectedProfessor
			this.selectedItem.discipline = this.selectedDiscipline
			this.selectedItem.value = this.selectedClassroom.value + ', ' + this.selectedProfessor.value + ', ' + this.selectedDiscipline.value
			for (let scheduleRowDataHolder in this.items) {
				for (let col in this.items[scheduleRowDataHolder]) {
					for (let item in this.items[scheduleRowDataHolder][col]) {
						let value = this.items[scheduleRowDataHolder][col][item]
						if (value.scheduleRowDataHolder === this.selectedItem.scheduleRowDataHolder && value.col === this.selectedItem.col && value.id === this.selectedItem.id) {
							console.log(this.items[scheduleRowDataHolder][col][item])
							this.items[scheduleRowDataHolder][col][item] = this.selectedItem
						}
					}
				}
			}
			this.selectedDiscipline = null
			this.selectedClassroom = null
			this.selectedProfessor = null
			this.dialog = false
		}
	},
	mounted() {
		SCHEDULE_API.get('getDefaultItems').then(resp => {
			console.log(resp)
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
			console.log(resp)
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
			console.log(resp)
			this.professors = resp.data
		}).catch(e => {
			console.log(e)
			alert(e.response.data.message)
		});
	},
	watch: {
		selectedProfessor(value) {
			if (value) {
				PROFESSOR_DISCIPLINE_API.post('getProfessorDisciplines', value).then(resp => {
					console.log(resp)
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
					<v-scheduleRowDataHolder>
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
					</v-scheduleRowDataHolder>
					<v-scheduleRowDataHolder>
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
					</v-scheduleRowDataHolder>
					<v-scheduleRowDataHolder>
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
					</v-scheduleRowDataHolder>
					<v-scheduleRowDataHolder>
						<v-col cols="4">
							<v-btn block color="info" @click="rollback">Отмена</v-btn>
						</v-col>
						<v-col cols="4"/>
						<v-col cols="4">
							<v-btn block color="success" @click="fillItem">Сохранить</v-btn>
						</v-col>
					</v-scheduleRowDataHolder>
				</v-card-text>
			</v-card>
		</v-dialog>
		<v-card-title>Составление расписания</v-card-title>
		<v-card-text>
			<br/>
			<h3 style="text-align: center">Первая неделя</h3>
			<br/>
			<v-scheduleRowDataHolder>
				<v-col cols="1">
					<v-scheduleRowDataHolder>
						<v-col cols="12">
							<v-data-table
									:headers="time"
									:items="times"
									class="elevation-1"
									dense
									hide-default-footer
									item-key="time"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
					</v-scheduleRowDataHolder>
				</v-col>
				<v-col cols="11">
					<v-scheduleRowDataHolder>
						<v-col cols="2">
							<v-data-table
									:headers="monday"
									:items="items[0].cols[0].items"
									class="elevation-1"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:scheduleRowDataHolder="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="tuesday"
									:items="items[0].cols[1].items"
									class="elevation-1"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:scheduleRowDataHolder="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="wednesday"
									:items="items[0].cols[2].items"
									class="elevation-1"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:scheduleRowDataHolder="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="thursday"
									:items="items[0].cols[3].items"
									class="elevation-1"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:scheduleRowDataHolder="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="friday"
									:items="items[0].cols[4].items"
									class="elevation-1"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:scheduleRowDataHolder="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="saturday"
									:items="items[0].cols[5].items"
									class="elevation-1"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:scheduleRowDataHolder="openCard"
							/>
						</v-col>
					</v-scheduleRowDataHolder>
				</v-col>
			</v-scheduleRowDataHolder>
			<br/>
			<h3 style="text-align: center">Вторая неделя</h3>
			<br/>
			<v-scheduleRowDataHolder>
				<v-col cols="1">
					<v-scheduleRowDataHolder>
						<v-col cols="12">
							<v-data-table
									:headers="time"
									:items="times"
									class="elevation-1"
									dense
									hide-default-footer
									item-key="time"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
					</v-scheduleRowDataHolder>
				</v-col>
				<v-col cols="11">
					<v-scheduleRowDataHolder>
						<v-col cols="2">
							<v-data-table
									:headers="monday"
									:items="items[1].cols[0].items"
									class="elevation-1"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:scheduleRowDataHolder="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="tuesday"
									:items="items[1].cols[1].items"
									class="elevation-1"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:scheduleRowDataHolder="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="wednesday"
									:items="items[1].cols[2].items"
									class="elevation-1"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:scheduleRowDataHolder="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="thursday"
									:items="items[1].cols[3].items"
									class="elevation-1"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:scheduleRowDataHolder="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="friday"
									:items="items[1].cols[4].items"
									class="elevation-1"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:scheduleRowDataHolder="openCard"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="saturday"
									:items="items[1].cols[5].items"
									class="elevation-1"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
									@dblclick:scheduleRowDataHolder="openCard"
							/>
						</v-col>
					</v-scheduleRowDataHolder>
				</v-col>
			</v-scheduleRowDataHolder>
			<v-scheduleRowDataHolder>
				<v-col cols="5"/>
				<v-col cols="2">
					<v-btn block color="success">Сохранить</v-btn>
				</v-col>
				<v-col cols="5"/>
			</v-scheduleRowDataHolder>
		</v-card-text>
	</v-card>
</template>

<style lang="css">
.v-data-table > .v-data-table__wrapper > table > tbody > tr > td, .v-data-table > .v-data-table__wrapper > table > thead > tr > td, .v-data-table > .v-data-table__wrapper > table > tfoot > tr > td {
	font-size: 8pt !important;
}
</style>