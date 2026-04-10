<script>
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
import {DICTIONARY_API, makeDownloadAction, PROFESSOR_API, SCHEDULE_API} from "@/axios/axios";
import Navigator from "@/views/components/Navigator.vue";

export default {
  components: {Navigator},
	name: 'GeneralSchedule',
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

    rules: {
      required: value => !!value || 'Обязательное поле',
    },
    typeSelector: 'student',
    semester: null,
    semesters: [],
    faculty: null,
    faculties: [],
    group: null,
    groups: [],
    subgroup: null,
    subgroups: [],
    department: null,
    departments: [],
    professor: null,
    professors: [],

    fileDownloading: false,
	}),
	mounted() {
    DICTIONARY_API.get(`getAllByTypes?${arrayToUriParams(['LESSON_TIME','SEMESTER','FACULTY','GROUP','DEPARTMENT','PROFESSOR','SUBGROUP'], "types")}`)
    .then(resp => {
      this.times = resp.data.LESSON_TIME
      //this.semesters = resp.data.SEMESTER
      this.faculties = resp.data.FACULTY
      this.groups = resp.data.GROUP
      this.subgroups = resp.data.SUBGROUP
      this.departments = resp.data.DEPARTMENT
      this.professors = resp.data.PROFESSOR
    }).then(() => SCHEDULE_API.post(
        'getSchedule',
        {},
        {
          params: {
            editable: false
          }
        }
    ).then(resp => {
      this.items = resp.data.rows
      this.semester = resp.data.semester
      this.semesters = resp.data.semesters
    }))
	},
  methods: {
    getRowColor,
    getSchedule() {
      SCHEDULE_API.post(
          'getSchedule',
          {},
          {
            params: {
              editable: false
            }
          }
      ).then(resp => {
        this.items = resp.data.rows
        this.semester = resp.data.semester
        this.semesters = resp.data.semesters
      })
    },
    getFilledSchedule() {
      if (this.typeSelector === 'student') {
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
                  editable: false
                }
              }
          ).then(resp => {
            this.items = resp.data.rows
          })
        }
      } else {
        if (this.professor !== null) {
          SCHEDULE_API.post(
              'getSchedule',
              {
                semester: this.semester,
                professor: this.professor,
              },
              {
                params: {
                  editable: false
                }
              }
          ).then(resp => {
            this.items = resp.data.rows
          })
        }
      }
    },
    async downloadFile() {
      this.fileDownloading = true

      try {
        if (this.typeSelector === 'student') {
          if (this.semester !== null && this.faculty !== null && this.group !== null) {
            const response = await SCHEDULE_API.post(
                'getSingleSchedule',
                {
                  semester: this.semester,
                  faculty: this.faculty,
                  group: this.group,
                  subgroup: this.subgroup
                },
                {
                  params: {
                    editable: false
                  },
                  responseType: 'blob',
                  headers: {
                    'Content-Type': 'application/json'
                  }
                }
            )
            makeDownloadAction(response, 'Расписание.xlsx')
          }
        } else {
          if (this.professor !== null) {
            const response = await SCHEDULE_API.post(
                'getSingleSchedule',
                {
                  semester: this.semester,
                  professor: this.professor,
                },
                {
                  params: {
                    editable: false
                  },
                  responseType: 'blob',
                  headers: {
                    'Content-Type': 'application/json'
                  }
                }
            )
            makeDownloadAction(response, 'Расписание.xlsx')
          }
        }
      } finally {
        this.fileDownloading = false
      }
    }
  },
  watch: {
    typeSelector(value) {
      if (value === 'student') {
        this.professor = null;
        this.department = null;
      } else {
        this.faculty = null;
        this.group = null;
        this.subgroup = null;
        this.department = null;
      }
      this.getSchedule();
    },
    semester(value, oldValue) {
      if (value && value !== oldValue) {
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
    subgroup(value) {
      if (value) {
        this.getFilledSchedule()
      } else {
        this.getFilledSchedule()
      }
    },
    department(value) {
      if (value) {
        PROFESSOR_API.post('getDepartmentProfessors', value).then(resp => {
          if (resp.data?.length > 0) {
            this.professor = null
            this.professors = resp.data
          }
        })
      } else {
        DICTIONARY_API.get('getAllByType', {
          params: {
            type: 'PROFESSOR'
          }
        }).then(resp => {
          this.professors = resp.data
        })
      }
    },
    professor(value) {
      if (value) {
        this.getFilledSchedule()
      }
    },
  }
}
</script>

<template>
	<v-container fluid>
    <Navigator/>
    <v-card>
      <v-card-title>
        {{semester !== null ? semester.value : ""}}
        <v-spacer></v-spacer>
        <v-btn
            v-if="(this.items != null && this.items !== 'undefined' && this.items.length !== 0) &&
             ((this.typeSelector === 'student' && this.semester !== null && this.faculty !== null && this.group !== null) || (this.typeSelector === 'professor' && this.professor !== null))"
            :loading="fileDownloading"
            :disabled="fileDownloading"
            color="primary"
            @click="downloadFile"
        >
          Скачать
          <template v-slot:loader>
            <v-progress-circular indeterminate color="white" size="20" />
          </template>
        </v-btn>
      </v-card-title>
      <v-card-text>
        <!--Форма поиска-->
        <div>
          <v-row>
            <v-col cols="12">
              <v-radio-group row mandatory v-model="typeSelector">
                <template v-slot:label>
                  <div>Режим отображения расписания:</div>
                </template>
                <v-radio label="Студента" color="indigo darken-3" value="student"/>
                <v-radio label="Преподавателя" color="indigo darken-3" value="professor"/>
              </v-radio-group>
            </v-col>
            <v-row v-if="typeSelector === 'student'">
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
            <v-row v-else>
              <v-col cols="6">
                <v-autocomplete
                    v-model="department"
                    :items="departments"
                    :rules="[rules.required]"
                    dense
                    hide-details
                    item-text="value"
                    item-value="id"
                    label="Выберите подразделение"
                    no-data-text="Нет данных"
                    outlined
                    clearable
                    return-object/>
              </v-col>
              <v-col cols="6">
                <v-autocomplete
                    v-model="professor"
                    :items="professors"
                    :rules="[rules.required]"
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
          </v-row>
        </div>
        <!--Расписание-->
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
                      :item-class="getRowColor"
                      :items="items[0].cols[0].items"
                      class="elevation-1 custom-table"
                      dense
                      hide-default-footer
                      item-key="id"
                      no-data-text="Данные отсутствуют"
                  />
                </v-col>
                <v-col cols="2">
                  <v-data-table
                      :headers="tuesday"
                      :item-class="getRowColor"
                      :items="items[0].cols[1].items"
                      class="elevation-1 custom-table"
                      dense
                      hide-default-footer
                      item-key="id"
                      no-data-text="Данные отсутствуют"
                  />
                </v-col>
                <v-col cols="2">
                  <v-data-table
                      :headers="wednesday"
                      :item-class="getRowColor"
                      :items="items[0].cols[2].items"
                      class="elevation-1 custom-table"
                      dense
                      hide-default-footer
                      item-key="id"
                      no-data-text="Данные отсутствуют"
                  />
                </v-col>
                <v-col cols="2">
                  <v-data-table
                      :headers="thursday"
                      :item-class="getRowColor"
                      :items="items[0].cols[3].items"
                      class="elevation-1 custom-table"
                      dense
                      hide-default-footer
                      item-key="id"
                      no-data-text="Данные отсутствуют"
                  />
                </v-col>
                <v-col cols="2">
                  <v-data-table
                      :headers="friday"
                      :item-class="getRowColor"
                      :items="items[0].cols[4].items"
                      class="elevation-1 custom-table"
                      dense
                      hide-default-footer
                      item-key="id"
                      no-data-text="Данные отсутствуют"
                  />
                </v-col>
                <v-col cols="2">
                  <v-data-table
                      :headers="saturday"
                      :item-class="getRowColor"
                      :items="items[0].cols[5].items"
                      class="elevation-1 custom-table"
                      dense
                      hide-default-footer
                      item-key="id"
                      no-data-text="Данные отсутствуют"
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
                      :item-class="getRowColor"
                      :items="items[1].cols[0].items"
                      class="elevation-1 custom-table"
                      dense
                      hide-default-footer
                      item-key="id"
                      no-data-text="Данные отсутствуют"
                  />
                </v-col>
                <v-col cols="2">
                  <v-data-table
                      :headers="tuesday"
                      :item-class="getRowColor"
                      :items="items[1].cols[1].items"
                      class="elevation-1 custom-table"
                      dense
                      hide-default-footer
                      item-key="id"
                      no-data-text="Данные отсутствуют"
                  />
                </v-col>
                <v-col cols="2">
                  <v-data-table
                      :headers="wednesday"
                      :item-class="getRowColor"
                      :items="items[1].cols[2].items"
                      class="elevation-1 custom-table"
                      dense
                      hide-default-footer
                      item-key="id"
                      no-data-text="Данные отсутствуют"
                  />
                </v-col>
                <v-col cols="2">
                  <v-data-table
                      :headers="thursday"
                      :item-class="getRowColor"
                      :items="items[1].cols[3].items"
                      class="elevation-1 custom-table"
                      dense
                      hide-default-footer
                      item-key="id"
                      no-data-text="Данные отсутствуют"
                  />
                </v-col>
                <v-col cols="2">
                  <v-data-table
                      :headers="friday"
                      :item-class="getRowColor"
                      :items="items[1].cols[4].items"
                      class="elevation-1 custom-table"
                      dense
                      hide-default-footer
                      item-key="id"
                      no-data-text="Данные отсутствуют"
                  />
                </v-col>
                <v-col cols="2">
                  <v-data-table
                      :headers="saturday"
                      :item-class="getRowColor"
                      :items="items[1].cols[5].items"
                      class="elevation-1 custom-table"
                      dense
                      hide-default-footer
                      item-key="id"
                      no-data-text="Данные отсутствуют"
                  />
                </v-col>
              </v-row>
            </v-col>
          </v-row>
        </div>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<style lang="css" scoped>

</style>