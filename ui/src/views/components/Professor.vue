/* eslint-disable */
<script>

import {DICTIONARY_API, PROFESSOR_API} from "@/axios/axios";
import {arrayToUriParams} from "@/constants/constants";
import Navigator from "@/views/components/Navigator.vue"

export default {
  components: {Navigator},
  /* eslint-disable-next-line */
	name: 'Professor',
	data: () => ({
		professor: null,
		professors: [],
		discipline: [],
		disciplines: [],
    department: null,
    departments: [],
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
      {
        text: 'Действия',
        value: 'actions',
        align: 'center',
        width: '10%'
      },
		],
		items: [],
	}),
	methods: {
    deleteLnk(item) {
      PROFESSOR_API.delete('deleteProfessorDiscipline', {
        params: {
          id: item.id
        },
      }).then(() => {
        this.fetchProfessorDisciplines(this.professor)
      })
    },
		addLnk() {
      PROFESSOR_API.post('addProfessorDiscipline', {
        professor: this.professor,
        disciplines: this.discipline
      }).then(() => {
        this.fetchProfessorDisciplines(this.professor)
        this.discipline = []
      })
		},
    updateDepartment() {
      PROFESSOR_API.post('updateDepartment', {
        professor: this.professor,
        department: this.department
      })
    },
		fetchProfessorDisciplines(professor) {
			PROFESSOR_API.post('getProfessorDisciplines', professor).then(resp => {
				this.items = resp.data
			})
		},
    fetchProfessorDepartment(professor) {
      PROFESSOR_API.post('getProfessorDepartment', professor).then(resp => {
        this.department = resp.data === '' ? null : resp.data.department
      })
    },
	},
	mounted() {
    DICTIONARY_API.get(`getAllByTypes?${arrayToUriParams(['PROFESSOR','DISCIPLINE','DEPARTMENT'], "types")}`).then(resp => {
      this.disciplines = resp.data.DISCIPLINE
      this.professors = resp.data.PROFESSOR
      this.departments = resp.data.DEPARTMENT
    });
	},
	watch: {
		professor(item) {
			if (item) {
				this.fetchProfessorDisciplines(item)
        this.fetchProfessorDepartment(item)
			}
		}
	}
}
</script>

<template>
  <v-container fluid>
    <Navigator/>
    <v-row>
      <v-col cols="1"/>
      <v-col cols="10">
        <v-card flat>
          <v-card-title>
            Настройка связей преподавателя
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="6">
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
            </v-row>
            <v-row>
              <v-col cols="6">
                <v-select
                    v-model="department"
                    :items="departments"
                    clearable
                    dense
                    hide-details
                    item-text="value"
                    item-value="id"
                    label="Выберите Подразделение"
                    no-data-text="Нет данных"
                    outlined
                    :disabled="professor === null"
                    return-object/>
              </v-col>
              <v-col cols="3">
                <v-btn block color="#2edb5c" @click="updateDepartment" :disabled="department === null || department === 'undefined'">Сохранить подразделение</v-btn>
              </v-col>
            </v-row>
            <v-row>
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
                    :disabled="professor === null"
                    return-object/>
              </v-col>
              <v-col cols="3">
                <v-btn block color="#2edb5c" @click="addLnk" :disabled="discipline.length === 0">Добавить связь</v-btn>
              </v-col>
            </v-row>
            <v-row v-if="professor !== null && professor !== undefined">
              <v-col cols="12">
                <v-text-field v-model="search" append-icon="mdi-magnify" hide-details label="Поиск" single-line/>
              </v-col>
            </v-row>
            <v-row v-if="professor !== null && professor !== undefined">
              <v-col cols="12">
                <v-data-table
                    item-class=""
                    :headers="headers"
                    :items="items"
                    :items-per-page="10"
                    :search="search"
                    class="elevation-1"
                    item-key="id">
                  <!--eslint-disable-next-line-->
                  <template v-slot:item.actions="{ item }">
                    <v-tooltip top>
                      <template v-slot:activator="{ on, attrs }">
                        <v-icon small v-bind="attrs" v-on="on" @click="deleteLnk(item)">mdi-delete</v-icon>
                      </template>
                      <span>Удалить связь</span>
                    </v-tooltip>
                  </template>
                </v-data-table>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="1"/>
    </v-row>
  </v-container>
</template>

<style lang="scss">

</style>