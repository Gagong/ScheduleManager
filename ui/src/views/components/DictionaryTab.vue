<script>
import {DICTIONARY_API} from "@/axios/axios";

export default {
    props: ['dictionary'],
    name: 'DictionaryTab',
    data : () => ({
        search: null,
        key: null,
        value: null,
        selectedDto: {
            key: null,
            value: null
        },
        blocked: true,
        rules: {
            required: value => !!value || 'Обязательное поле',
            counter: value => (value && value.length <= 50) || 'Максимум 50 символов',
            keyCounter: value => (value && value.length <= 10) || 'Максимум 10 символов'
        },
        headers: [
            {
                text: 'ID',
                align: 'center',
                sortable: true,
                value: 'id'
            },
            {
                text: 'Тип',
                align: 'center',
                sortable: true,
                value: 'type'
            },
            {
                text: 'Ключ',
                align: 'center',
                sortable: true,
                value: 'key'
            },
            {
                text: 'Значение',
                align: 'center',
                sortable: true,
                value: 'value'
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
        addNewDictionaryValue() {
            const dto = {
                type: this.dictionary.key,
                key: this.key,
                value: this.value
            }
            DICTIONARY_API.post('create', dto).then(resp => {
                console.log(resp)
                this.fetchDictionaryTable()
                this.key = null;
                this.value = null;
            }).catch(e => {
                console.log(e)
                alert(e.response.data.message)
            })
        },
        updateDictionaryValue() {
            DICTIONARY_API.patch('update', this.selectedDto).then(resp => {
                console.log(resp)
                this.fetchDictionaryTable()
                this.blocked = true;
                this.key = null;
                this.value = null;
            }).catch(e => {
                console.log(e)
                alert(e.response.data.message)
            })
        },
        updateValue(event, data) {
            this.selectedDto = data.item;
            this.blocked = false;
        },
        fetchDictionaryTable() {
            DICTIONARY_API.get('getAllByType', {
                params: {
                    type: this.dictionary.key
                }
            }).then(resp => {
                console.log(resp)
                this.items = resp.data
            }).catch(e => {
                console.log(e)
                alert(e.response.data.message)
            })
        }
    },
    mounted() {
        this.fetchDictionaryTable();
    },
}
</script>

<template>
    <v-card>
        <v-card-title>
            {{dictionary.value}}
        </v-card-title>
        <v-card-text>
            <v-row>
                <v-col cols="12">
                    <h4>Добавить новое значение</h4>
                </v-col>
            </v-row>
            <v-row>
                <v-col cols="12">
                    <v-row>
                        <v-col cols="4">
                            <v-text-field
                                    dense
                                    clearable
                                    outlined
                                    counter
                                    maxlength="10"
                                    label="Ключ"
                                    v-model="key"
                                    :rules="[rules.keyCounter, rules.required]"
                            />
                        </v-col>
                        <v-col cols="6">
                            <v-text-field
                                    dense
                                    clearable
                                    outlined
                                    counter
                                    maxlength="50"
                                    label="Значение"
                                    v-model="value"
                                    :rules="[rules.counter, rules.required]"
                            />
                        </v-col>
                        <v-col cols="2">
                            <v-btn color="#2edb5c" block @click="this.addNewDictionaryValue">Добавить</v-btn>
                        </v-col>
                    </v-row>
                </v-col>
            </v-row>
            <v-row>
                <v-col cols="12">
                    <h4>Изменить значение</h4>
                </v-col>
            </v-row>
            <v-row>
                <v-col cols="12">
                    <v-row>
                        <v-col cols="4">
                            <v-text-field
                                dense
                                clearable
                                outlined
                                counter
                                maxlength="10"
                                label="Ключ"
                                v-model="selectedDto.key"
                                :disabled="blocked"
                                :rules="[rules.keyCounter, rules.required]"
                            />
                        </v-col>
                        <v-col cols="6">
                            <v-text-field
                                dense
                                clearable
                                outlined
                                counter
                                maxlength="50"
                                label="Значение"
                                v-model="selectedDto.value"
                                :disabled="blocked"
                                :rules="[rules.counter, rules.required]"
                            />
                        </v-col>
                        <v-col cols="2">
                            <v-btn color="#ffd000" block @click="this.updateDictionaryValue" :disabled="blocked">Изменить</v-btn>
                        </v-col>
                    </v-row>
                </v-col>
            </v-row>
            <v-row>
                <v-col cols="12">
                    <v-text-field v-model="search" append-icon="mdi-magnify" label="Поиск" single-line hide-details/>
                </v-col>
            </v-row>
            <v-row>
                <v-col cols="12">
                    <v-data-table show-select :search="search" item-key="id" class="elevation-1" :headers="headers" :items="items" :items-per-page="10" no-data-text="Данные отсутствуют" @dblclick:row="updateValue"/>
                </v-col>
            </v-row>
        </v-card-text>
    </v-card>
</template>

<style scoped lang="scss">

</style>