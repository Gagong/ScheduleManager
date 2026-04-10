/* eslint-disable */
<script>
import {DICTIONARY_API} from "@/axios/axios";

export default {
	props: ['dictionary', 'canEdit'],
	name: 'DictionaryTab',
	data: () => ({
		search: null,
    archiveSearch: null,
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
			keyCounter: value => (value && value.length <= 50) || 'Максимум 50 символов'
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
        text: 'Создано',
        align: 'center',
        sortable: true,
        value: 'createdBy'
      },
			{
				text: 'Изменено',
				align: 'center',
				sortable: true,
				value: 'updateDateTime'
			},
      {
        text: 'Изменено',
        align: 'center',
        sortable: true,
        value: 'updatedBy'
      },
      {
        text: 'Действия',
        value: 'actions',
        align: 'center',
        width: '10%'
      },
		],
    archiveHeaders: [
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
        text: 'Создано',
        align: 'center',
        sortable: true,
        value: 'createdBy'
      },
      {
        text: 'Изменено',
        align: 'center',
        sortable: true,
        value: 'updateDateTime'
      },
      {
        text: 'Изменено',
        align: 'center',
        sortable: true,
        value: 'updatedBy'
      },
    ],
		items: [],
    archiveItems: [],
	}),
	methods: {
    archiveDictionary(item) {
      const dto = {
        id: item.id,
        type: item.type,
        key: item.key,
        value: item.value,
        active: false
      }
      DICTIONARY_API.patch('update', dto).then(() => {
        this.fetchDictionaryTable()
      })
    },
		addNewDictionaryValue() {
			const dto = {
				type: this.dictionary.key,
				key: this.key,
				value: this.value
			}
			DICTIONARY_API.post('create', dto).then(() => {
				this.fetchDictionaryTable()
				this.key = null;
				this.value = null;
			})
		},
		updateDictionaryValue() {
			DICTIONARY_API.patch('update', this.selectedDto).then(() => {
				this.fetchDictionaryTable()
				this.blocked = true;
				this.key = null;
				this.value = null;
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
				this.items = resp.data
			}).then(() => DICTIONARY_API.get('getAllByType', {
        params: {
          type: this.dictionary.key,
          onlyActive: false
        }
      }).then(resp => {
        this.archiveItems = resp.data
      }))
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
			<v-row v-if="canEdit">
				<v-col cols="12">
					<h4>Добавить новое значение</h4>
				</v-col>
			</v-row>
			<v-row v-if="canEdit">
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
									maxlength="50"
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
							<v-btn block color="success" @click="this.addNewDictionaryValue">Добавить</v-btn>
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
									:disabled="true"
									:rules="[rules.keyCounter, rules.required]"
									clearable
									counter
									dense
									label="Ключ"
									maxlength="50"
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
							<v-btn :disabled="blocked" block color="warning" @click="this.updateDictionaryValue">Изменить</v-btn>
						</v-col>
					</v-row>
				</v-col>
			</v-row>
      <v-row>
        <v-col cols="12">
          <h4>Активные значения</h4>
        </v-col>
      </v-row>
			<v-row>
				<v-col cols="12">
					<v-text-field v-model="search" append-icon="mdi-magnify" hide-details label="Поиск" single-line/>
				</v-col>
			</v-row>
			<v-row>
				<v-col cols="12">
					<v-data-table
							:headers="headers"
							:items="items"
							:items-per-page="10"
							:search="search"
							class="elevation-1"
							item-key="id"
              @dblclick:row="updateValue">
            <!--eslint-disable-next-line-->
            <template v-slot:item.actions="{ item }">
              <v-tooltip top>
                <template v-slot:activator="{ on, attrs }">
                  <v-icon v-if="canEdit" small v-bind="attrs" v-on="on" @click="archiveDictionary(item)">mdi-delete</v-icon>
                </template>
                <span>Переместить в архив</span>
              </v-tooltip>
            </template>
          </v-data-table>
				</v-col>
			</v-row>
      <v-row v-if="canEdit">
        <v-col cols="12">
          <h4>Архивные значения</h4>
        </v-col>
      </v-row>
      <v-row v-if="canEdit">
        <v-col cols="12">
          <v-text-field v-model="archiveSearch" append-icon="mdi-magnify" hide-details label="Поиск" single-line/>
        </v-col>
      </v-row>
      <v-row v-if="canEdit">
        <v-col cols="12">
          <v-data-table
              :headers="archiveHeaders"
              :items="archiveItems"
              :items-per-page="10"
              :search="archiveSearch"
              class="elevation-1"
              item-key="id"
          />
        </v-col>
      </v-row>
		</v-card-text>
	</v-card>
</template>

<style lang="scss" scoped>

</style>