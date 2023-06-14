<script>

import {DICTIONARY_API} from "@/axios/axios";

export default {
    name: 'ProfessorDiscipline',
    data : () => ({
        professor: null,
        professors: [],
        discipline: [],
        disciplines: [],
    }),
    methods: {

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
                        dense
                        outlined
                        hide-details
                        clearable
                        return-object
                        item-value="id"
                        item-text="value"
                        v-model="professor"
                        :items="professors"
                        label="Выберите Преподавателя"/>
                </v-col>
                <v-col cols="6">
                    <v-select
                            dense
                            outlined
                            hide-details
                            clearable
                            multiple
                            return-object
                            item-value="id"
                            item-text="value"
                            v-model="discipline"
                            :items="disciplines"
                            label="Выберите Дисциплину"/>
                </v-col>
                <v-col cols="2">
                    <v-btn color="#2edb5c" block>Добавить связь</v-btn>
                </v-col>
            </v-row>
            <v-row>

            </v-row>
        </v-card-text>
    </v-card>
</template>

<style scoped lang="scss">

</style>