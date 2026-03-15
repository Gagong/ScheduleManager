<script>

import {DICTIONARY_API, PROFESSOR_API, SCHEDULE_API} from "@/axios/axios";
import getRowColor, {
  arrayToUriParams,
  FRIDAY,
  MONDAY,
  SATURDAY,
  THURSDAY,
  TIME,
  TUESDAY,
  WEDNESDAY
} from "@/constants/constants";
import Navigator from "@/views/components/Navigator.vue";

export default {
  components: {Navigator},
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
		times: [],
		dialog: false,
		selectedItem: {
			id: null,
			row: null,
			col: null,
			value: null,
			classroom: null,
			professor: null,
			discipline: null,
			disciplineType: null,
      semester: null,
      faculty: null,
      group: null,
      subgroup: null,
      times: null,
		},
    rollbackItem: {
      id: null,
      row: null,
      col: null,
      value: null,
      classroom: null,
      professor: null,
      discipline: null,
      disciplineType: null,
      semester: null,
      faculty: null,
      group: null,
      subgroup: null,
      times: null
    },
		selectedDiscipline: null,
		selectedClassroom: null,
		selectedProfessor: null,
		selectedDisciplineType: null,
    selectedSubGroup: null,
		disciplines: [],
		classrooms: [],
		professors: [],
		disciplineTypes: [],
    subgroups: [],
		rules: {
			required: value => !!value || 'Обязательное поле',
		},
    semester: null,
    semesters: [],
    faculty: null,
    faculties: [],
    group: null,
    subgroup: null,
    groups: [],
	}),
	methods: {
		getRowColor,
		openCard(event, data) {
			this.selectedItem = data.item
			this.selectedClassroom = data.item.classroom
			this.selectedProfessor = data.item.professor
			this.selectedDiscipline = data.item.discipline
			this.selectedDisciplineType = data.item.disciplineType
      this.selectedSubGroup = data.item.subgroup

      this.rollbackItem.classroom = data.item.classroom
      this.rollbackItem.professor = data.item.professor
      this.rollbackItem.discipline = data.item.discipline
      this.rollbackItem.disciplineType = data.item.disciplineType
      this.rollbackItem.semester = data.item.semester
      this.rollbackItem.faculty = data.item.faculty
      this.rollbackItem.group = data.item.group
      this.rollbackItem.subgroup = data.item.subgroup
      this.rollbackItem.times = data.item.times
			this.dialog = true
		},
		rollback() {
			this.selectedDiscipline = null
			this.selectedClassroom = null
			this.selectedProfessor = null
			this.selectedDisciplineType = null
      this.selectedSubGroup = null
      this.selectedItem = this.rollbackItem
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
      this.selectedItem.subgroup = this.selectedSubGroup
      this.selectedItem.semester = this.semester
      this.selectedItem.faculty = this.faculty
      this.selectedItem.group = this.group
			this.selectedItem.value = this.selectedClassroom.value + ', ' + this.selectedProfessor.value + ', ' + this.selectedDisciplineType.value + ', ' + this.selectedDiscipline.value + (this.selectedSubGroup === null || this.selectedSubGroup.value === '' ? '' : ' (' + this.selectedSubGroup.value + ')')
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
      this.selectedSubGroup = null
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
                    this.selectedItem.subgroup = null
										this.selectedItem.value = "Нажмите для заполнения"
										this.items[row][col][item][data][v] = this.selectedItem
										this.selectedDiscipline = null
										this.selectedClassroom = null
										this.selectedProfessor = null
										this.selectedDisciplineType = null
                    this.selectedSubGroup = null
										this.dialog = false
									})
								}
							}
						}
					}
				}
			}
		},
		saveSchedule() {
      SCHEDULE_API.post('save', this.items)
          .then(() => this.getFilledSchedule())
		},
    getFilledSchedule() {
      if (this.semester !== null && this.faculty !== null && this.group !== null) {
        SCHEDULE_API.post(
            'getSchedule',
            {
              semester: this.semester,
              faculty: this.faculty,
              group: this.group,
              subgroup: this.subgroup
            },
            {
              params: {
                editable: true
              }
            }
        ).then(resp => {
          this.items = resp.data.rows
        })
      }
    }
	},
	mounted() {
    DICTIONARY_API.get(`getAllByTypes?${arrayToUriParams(['DISCIPLINE_TYPE','SEMESTER','FACULTY','GROUP','SUBGROUP','LESSON_TIME'], "types")}`).then(resp => {
      this.disciplineTypes = resp.data.DISCIPLINE_TYPE
      this.semesters = resp.data.SEMESTER
      this.faculties = resp.data.FACULTY
      this.groups = resp.data.GROUP
      this.subgroups = resp.data.SUBGROUP
      this.times = resp.data.LESSON_TIME
    })
    SCHEDULE_API.get("getCurrentSemester").then(resp => this.semester = resp.data)
	},
	watch: {
		selectedProfessor(value) {
			if (value) {
				PROFESSOR_API.post('getProfessorDisciplines', value).then(resp => {
					this.disciplines = resp.data.map(data => data.discipline)
				})
			}
		},
    semester(value) {
      if (value) {
        this.getFilledSchedule()
      }
    },
    faculty(value) {
      if (value) {
        this.getFilledSchedule()
      }
    },
    group(value) {
      if (value) {
        this.getFilledSchedule()
      }
    },
    // eslint-disable-next-line
    subgroup(value) {
      this.getFilledSchedule()
    },
    selectedItem(value) {
      if (value) {
        SCHEDULE_API.post('getFreeClassRoomsAndProfessors', value).then(resp => {
          this.classrooms = resp.data.CLASSROOM
          this.professors = resp.data.PROFESSOR
        })
      } else {
        DICTIONARY_API.get(`getAllByTypes?${arrayToUriParams(['CLASSROOM','PROFESSOR'], "types")}`).then(resp => {
          this.classrooms = resp.data.CLASSROOM
          this.professors = resp.data.PROFESSOR
        })
      }
    }
	}
}
</script>

<template>
  <v-container fluid>
    <Navigator/>
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
                    no-data-text="Нет свободных аудиторий"
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
                    no-data-text="Нет свободных преподавателей"
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
              <v-col cols="12">
                <v-autocomplete
                    v-model="selectedSubGroup"
                    :items="subgroups"
                    clearable
                    dense
                    hide-details
                    item-text="value"
                    item-value="id"
                    label="Выберите подгруппу"
                    no-data-text="Для всей группы"
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
        <v-row>
          <v-col cols="4">
            <v-autocomplete
                v-model="semester"
                :items="semesters"
                :rules="[rules.required]"
                dense
                hide-details
                item-text="value"
                item-value="id"
                label="Выберите семестр"
                no-data-text="Нет данных"
                outlined
                return-object/>
          </v-col>
          <v-col cols="4">
            <v-autocomplete
                v-model="faculty"
                :items="faculties"
                :rules="[rules.required]"
                dense
                hide-details
                item-text="value"
                item-value="id"
                label="Выберите факультет"
                no-data-text="Нет данных"
                outlined
                return-object/>
          </v-col>
          <v-col cols="2">
            <v-autocomplete
                v-model="group"
                :items="groups"
                :rules="[rules.required]"
                dense
                hide-details
                item-text="value"
                item-value="id"
                label="Выберите группу"
                no-data-text="Нет данных"
                outlined
                return-object/>
          </v-col>
          <v-col cols="2">
            <v-autocomplete
                v-model="subgroup"
                :items="subgroups"
                dense
                hide-details
                item-text="value"
                item-value="id"
                label="Выберите подгруппу"
                no-data-text="Нет данных"
                outlined
                clearable
                return-object/>
          </v-col>
        </v-row>
        <div v-if="this.items != null && this.items !== 'undefined' && this.items.length !== 0">
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
        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<style lang="css">
.custom-table td {
	font-size: 8pt !important;
	height: 85px !important;
}

.discipline_type_715 {
  background-color: rgba(255, 255, 255, 0.05); /* #ffffff */
}

.discipline_type_716_717 {
  background-color: rgba(45, 213, 255, 0.05); /* #2dd5ff */
}

.discipline_type_718 {
  background-color: rgba(38, 255, 62, 0.05); /* #26ff3e */
}

.discipline_type_719 {
  background-color: rgba(255, 81, 60, 0.05); /* #ff513c */
}

.discipline_type_720 {
  background-color: rgba(255, 221, 43, 0.05); /* #ffdd2b */
}

.discipline_type_721 {
  background-color: rgba(172, 75, 255, 0.05); /* #ac4bff */
}

.discipline_type_722 {
  background-color: rgba(26, 255, 225, 0.05); /* #1affe1 */
}
</style>