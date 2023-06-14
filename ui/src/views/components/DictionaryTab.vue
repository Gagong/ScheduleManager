<script>
import {DICTIONARY_API} from "@/axios/axios";

export default {
	props: ['dictionary'],
	name: 'DictionaryTab',
	data: () => ({
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
			{{ dictionary.value }}
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
									v-model="key"
									:rules="[rules.keyCounter, rules.required]"
									clearable
									counter
									dense
									label="Ключ"
									maxlength="10"
									outlined
							/>
						</v-col>
						<v-col cols="6">
							<v-text-field
									v-model="value"
									:rules="[rules.counter, rules.required]"
									clearable
									counter
									dense
									label="Значение"
									maxlength="50"
									outlined
							/>
						</v-col>
						<v-col cols="2">
							<v-btn block color="#2edb5c" @click="this.addNewDictionaryValue">Добавить</v-btn>
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
									v-model="selectedDto.key"
									:disabled="blocked"
									:rules="[rules.keyCounter, rules.required]"
									clearable
									counter
									dense
									label="Ключ"
									maxlength="10"
									outlined
							/>
						</v-col>
						<v-col cols="6">
							<v-text-field
									v-model="selectedDto.value"
									:disabled="blocked"
									:rules="[rules.counter, rules.required]"
									clearable
									counter
									dense
									label="Значение"
									maxlength="50"
									outlined
							/>
						</v-col>
						<v-col cols="2">
							<v-btn :disabled="blocked" block color="#ffd000" @click="this.updateDictionaryValue">Изменить</v-btn>
						</v-col>
					</v-row>
				</v-col>
			</v-row>
			<v-row>
				<v-col cols="12">
					<v-text-field v-model="search" append-icon="mdi-magnify" hide-details label="Поиск" single-line/>
				</v-col>
			</v-row>
			<v-row>
				<v-col cols="12">
					<v-data-table :headers="headers" :items="items" :items-per-page="10" :search="search" class="elevation-1" item-key="id"
					              no-data-text="Данные отсутствуют" show-select @dblclick:row="updateValue"/>
				</v-col>
			</v-row>
		</v-card-text>
	</v-card>
</template>

<style lang="scss" scoped>

</style>